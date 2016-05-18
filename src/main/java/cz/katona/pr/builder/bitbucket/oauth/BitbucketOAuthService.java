package cz.katona.pr.builder.bitbucket.oauth;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import cz.katona.pr.builder.bitbucket.BitbucketException;
import cz.katona.pr.builder.bitbucket.BitbucketResources;
import cz.katona.pr.builder.bitbucket.BitbucketService;
import cz.katona.pr.builder.bitbucket.model.CommentAdd;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Service that allows various operations on bitbucket API, authentication is done through OAuth.
 * This bean is enabled only if these properties are set:
 * <ul>
 *     <li>bitbucket.oauth.clientId</li>
 *     <li>bitbucket.oauth.clientSecret</li>
 * </ul>
 */
@Component
@ConditionalOnProperty(name = {"clientId", "clientSecret"}, prefix = "bitbucket.oauth")
public class BitbucketOAuthService implements BitbucketService {

    private final String bitbucketRestEndpoint;
    private final BitbucketOAuthSettings oAuthSettings;
    private final ObjectMapper objectMapper;

    @Autowired
    public BitbucketOAuthService(@Value("${bitbucket.rest.uri}") String bitbucketRestEndpoint,
                                 BitbucketOAuthSettings oAuthSettings, ObjectMapper objectMapper) {
        notEmpty(bitbucketRestEndpoint, "Bitbucket rest uri can't be empty!");
        notNull(oAuthSettings, "OAuth settings can't be null!");
        notNull(objectMapper, "Object mapper can't be null!");

        this.bitbucketRestEndpoint = bitbucketRestEndpoint;
        this.oAuthSettings = oAuthSettings;
        this.objectMapper = objectMapper;
    }

    @Override
    public void createComment(String repositoryFullName, Long pullId, String commentContent, Long commentParentId) {
        notEmpty(repositoryFullName, "Repository can't be empty");
        notNull(pullId, "Pull id can't be null!");

        final OAuthRequest request = buildCommentRequest(repositoryFullName, pullId, commentContent, commentParentId);

        final Response response = sendRequest(request);
        if (!response.isSuccessful()) {
            throw new BitbucketException("Error creating comment via bitbucket API, response=" + response);
        }
    }

    OAuthRequest buildCommentRequest(String repositoryFullName, Long pullId, String commentContent, Long commentParentId) {
        CommentAdd comment = new CommentAdd(commentContent, commentParentId);

        final OAuthRequest request = new OAuthRequest(Verb.POST,
                BitbucketResources.COMMENTS_RESOURCE.expand(bitbucketRestEndpoint, repositoryFullName, pullId).toString(),
                oAuthSettings.getOAuthService());
        request.addHeader(AUTHORIZATION, "Bearer " + oAuthSettings.getAccessToken().getAccessToken());
        request.addHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.addPayload(writeValueAsJson(comment));
        return request;
    }

    Response sendRequest(OAuthRequest request) {
        return request.send();
    }

    private String writeValueAsJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new BitbucketException("Unable to serialize " + object + " to JSON.", e);
        }
    }
}
