package cz.katona.pr.builder.bamboo.oauth;

import static cz.katona.pr.builder.bamboo.BambooResources.BRANCH_CREATE_RESOURCE;
import static cz.katona.pr.builder.bamboo.BambooResources.BRANCH_RESOURCE;
import static cz.katona.pr.builder.bamboo.BambooResources.QUEUE_BRANCH_JOB;
import static cz.katona.pr.builder.bamboo.BambooResources.BRANCH_ENABLE_RESOURCE;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import cz.katona.pr.builder.bamboo.BambooException;
import cz.katona.pr.builder.bamboo.BambooService;
import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bamboo.model.JobQueued;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Service that allows various operations on bamboo API, authentication is done through OAuth1a.
 * This bean is enabled only if {@link BambooOAuthSettings} is enabled
 */
@Component
@ConditionalOnBean(value = BambooOAuthSettings.class)
public class BambooOAuthService implements BambooService {

    private final OAuth10aService service;
    private final OAuth1AccessToken accessToken;
    private final ObjectMapper objectMapper;
    private final String bambooRestEndpoint;

    @Autowired
    public BambooOAuthService(@Value("${bamboo.rest.uri}") String bambooRestEndpoint,
                              BambooOAuthSettings bambooOAuthSettings,
                              ObjectMapper objectMapper) {
        notEmpty(bambooRestEndpoint, "Bamboo rest endpoint can't be empty!");
        notNull(bambooOAuthSettings, "Bamboo oauth settings can't be null!");
        notNull(objectMapper, "Object mapper can't be null!");

        this.bambooRestEndpoint = bambooRestEndpoint;
        this.objectMapper = objectMapper;
        this.service = bambooOAuthSettings.getOAuthService();
        this.accessToken = bambooOAuthSettings.getAccessToken();
    }

    @Override
    public Branch getBranch(String planId, String branchName) {
        notEmpty(planId, "Plan id can't be empty!");
        notEmpty(branchName, "Branch name can't be empty!");

        final OAuthRequest request = new OAuthRequest(Verb.GET, BRANCH_RESOURCE.expand(bambooRestEndpoint,
                planId, branchName).toString(), service);

        final Response response = signAndSend(request);
        int statusCode = response.getCode();
        if (statusCode == HttpStatus.OK.value()) {
            //branch exists
            return readValue(response.getBody(), Branch.class);
        } else if (statusCode == HttpStatus.NO_CONTENT.value()) {
            //branch doesn't exist
            return null;
        } else {
            throw new BambooException("Unable to fetch branch '" + branchName + "', full response " + response);
        }
    }

    @Override
    public Branch createBranch(String planId, String branchName) {
        notEmpty(planId, "Plan id can't be empty!");
        notEmpty(branchName, "Branch name can't be empty!");

        final OAuthRequest request = new OAuthRequest(Verb.PUT,
                BRANCH_CREATE_RESOURCE.expand(bambooRestEndpoint, planId, branchName, branchName).toString(), service);

        final Response response = signAndSend(request);
        int statusCode = response.getCode();
        if (statusCode != HttpStatus.OK.value()) {
            throw new BambooException("Unable to create branch '" + branchName + "', full response " + response);
        }
        return readValue(response.getBody(), Branch.class);

    }

    @Override
    public void enableBranch(String planIdWithBranch) {
        notEmpty(planIdWithBranch, "Plan id with branch can't be empty!");

        final OAuthRequest request = new OAuthRequest(Verb.POST,
                BRANCH_ENABLE_RESOURCE.expand(bambooRestEndpoint, planIdWithBranch).toString(), service);

        final Response response = signAndSend(request);
        int statusCode = response.getCode();
        if (statusCode != HttpStatus.NO_CONTENT.value()) {
            throw new BambooException("Unable to enable plan '" + planIdWithBranch + "', full response " + response);
        }
    }

    @Override
    public JobQueued queueJob(String planIdWithBranch) {
        notEmpty(planIdWithBranch, "Plan id with branch can't be empty!");

        final OAuthRequest request = new OAuthRequest(Verb.POST,
                QUEUE_BRANCH_JOB.expand(bambooRestEndpoint, planIdWithBranch).toString(), service);

        final Response response = signAndSend(request);
        int statusCode = response.getCode();
        if (statusCode != HttpStatus.OK.value()) {
            throw new BambooException("Unable to queue job '" + planIdWithBranch + "', full response " + response);
        }

        return readValue(response.getBody(), JobQueued.class);
    }

    private <T> T readValue(String body, Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (IOException e) {
            throw new BambooException("Cannot read from json=" + body + " for type=" + type.getName(), e);
        }
    }

    Response signAndSend(OAuthRequest request) {
        service.signRequest(accessToken, request);
        return request.send();
    }
}
