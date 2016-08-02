package cz.katona.pr.builder.bitbucket;

import static cz.katona.pr.builder.bitbucket.BitbucketResources.COMMENTS_RESOURCE;
import static cz.katona.pr.builder.bitbucket.BitbucketResources.MAIN_BRANCH_RESOURCE;
import static cz.katona.pr.builder.bitbucket.BitbucketResources.NO_BRANCH_SET_RESPONSE;
import static cz.katona.pr.builder.util.RequestUtils.getHttpHeadersWithAuth;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.katona.pr.builder.bitbucket.model.CommentAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Service that allows various operations on bitbucket API, authentication is basic.
 * This bean is enabled only if these properties are set:
 * <ul>
 *     <li>bitbucket.basic.user</li>
 *     <li>bitbucket.basic.password</li>
 * </ul>
 */
@Component
@ConditionalOnProperty(name = {"user", "password"}, prefix = "bitbucket.basic")
public class BitbucketBasicAuthService implements BitbucketService {

    //headers which contain basic auth
    private final HttpHeaders headers;

    private final String bitbucketRestEndpoint;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public BitbucketBasicAuthService(@Value("${bitbucket.rest.uri}") String bitbucketRestEndpoint,
                                     @Value("${bitbucket.basic.user}") String username,
                                     @Value("${bitbucket.basic.password}") String password,
                                     RestTemplate restTemplate, ObjectMapper objectMapper) {
        notEmpty(bitbucketRestEndpoint, "Bitbucket rest endpoint can't be empty!");
        notEmpty(username, "Username can't be empty!");
        notNull(restTemplate, "Rest template can't be null!");
        notNull(objectMapper, "Object mapper can't be null!");

        this.bitbucketRestEndpoint = bitbucketRestEndpoint;
        this.headers = getHttpHeadersWithAuth(username, password);
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void createComment(String repositoryFullName, Long pullId, String commentContent, Long commentParentId) {
        CommentAdd comment = new CommentAdd(commentContent, commentParentId);

        HttpEntity<CommentAdd> request = new HttpEntity<>(comment, headers);
        ResponseEntity<Void> exchange = restTemplate.exchange(COMMENTS_RESOURCE.expand(
                bitbucketRestEndpoint, repositoryFullName, pullId).toString(),
                HttpMethod.POST, request, Void.class);

        if (exchange.getStatusCode() != HttpStatus.OK) {
            throw new BitbucketException("Error creating comment via bitbucket API, full response " + exchange);
        }
    }

    @Override
    public String getMainBranch(String repositoryFullName) {
        notEmpty(repositoryFullName, "repositoryFullName can't be empty!");

        HttpEntity<CommentAdd> request = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = restTemplate.exchange(MAIN_BRANCH_RESOURCE.expand(
                bitbucketRestEndpoint, repositoryFullName).toString(),
                HttpMethod.GET, request, String.class);

        String mainBranchName = null;
        HttpStatus httpStatus = exchange.getStatusCode();
        if (httpStatus == HttpStatus.OK) {
            try {
                JsonNode responseTree = objectMapper.readTree(exchange.getBody());
                mainBranchName = responseTree.get("name").asText();
            } catch (IOException ioex) {
                throw new BitbucketException("Unable to deserialize response from bitbucket API", ioex);
            }
        } else if (httpStatus == HttpStatus.NOT_FOUND && NO_BRANCH_SET_RESPONSE.equals(exchange.getBody())) {
            mainBranchName = null; //no main branch set, but valid call
        } else {
            throw new BitbucketException("Error getting information about main branch via bitbucket API, response=" + exchange);
        }
        return mainBranchName;
    }
}
