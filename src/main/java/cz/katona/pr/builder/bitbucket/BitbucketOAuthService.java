package cz.katona.pr.builder.bitbucket;

import static org.apache.http.entity.ContentType.*;
import static org.springframework.http.HttpHeaders.*;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import cz.katona.pr.builder.bitbucket.model.CommentAdd;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = {"clientId", "clientSecret"}, prefix = "bitbucket.oauth")
public class BitbucketOAuthService implements BitbucketService {

    private static final String BITBUCKET_OAUTH_ENDPOINT = "https://bitbucket.org/site/oauth2/access_token";

    private final String restUri;
    private final String clientId;
    private final String clientSecret;

    @Autowired
    public BitbucketOAuthService(@Value("${bitbucket.rest.uri}") String restUri,
                                 @Value("${bitbucket.oauth.clientId}") String clientId,
                                 @Value("${bitbucket.oauth.clientSecret}") String clientSecret) {
        this.restUri = restUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public void createComment(String repositoryFullName, Long pullId, String commentContent, Long commentParentId) {
        CommentAdd comment = new CommentAdd(commentContent, commentParentId);

        try {
            HttpResponse<String> response =
                    Unirest.post(BitbucketResources.COMMENTS_RESOURCE.expand(restUri, repositoryFullName, pullId).toString())
                            .header(AUTHORIZATION, "Bearer " + getAccessToken())
                            .body(comment)
                            .asString();
            if (response.getStatus() != HttpStatus.SC_OK) {
                throw new BitbucketException("Error creating comment via bitbucket API, status=" + response.getStatus());
            }
        } catch (UnirestException e) {
            throw new BitbucketException("Error creating comment via bitbucket API", e);
        }

    }

    private String getAccessToken() {
        try {
            HttpResponse<JsonNode> accessTokenResponse = Unirest.post(BITBUCKET_OAUTH_ENDPOINT)
                    .basicAuth(clientId, clientSecret)
                    .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.getMimeType()).body("grant_type=client_credentials")
                    .asJson();
            if (accessTokenResponse.getStatus() != HttpStatus.SC_OK) {
                throw new BitbucketException("Unable to retrieve access token, status=" + accessTokenResponse.getStatus());
            }
            return accessTokenResponse.getBody().getObject().getString("access_token");
        } catch (UnirestException ex) {
            throw new BitbucketException("Unable to retrieve access token", ex);

        }
    }
}