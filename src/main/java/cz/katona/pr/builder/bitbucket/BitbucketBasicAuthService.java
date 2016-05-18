package cz.katona.pr.builder.bitbucket;

import static cz.katona.pr.builder.bitbucket.BitbucketResources.COMMENTS_RESOURCE;
import static cz.katona.pr.builder.util.RequestUtils.getHttpHeadersWithAuth;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

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

    @Autowired
    public BitbucketBasicAuthService(@Value("${bitbucket.rest.uri}") String bitbucketRestEndpoint,
                                     @Value("${bitbucket.basic.user}") String username,
                                     @Value("${bitbucket.basic.password}") String password,
                                     RestTemplate restTemplate) {
        notEmpty(bitbucketRestEndpoint, "Bitbucket rest endpoint can't be empty!");
        notEmpty(username, "Username can't be empty!");
        notNull(restTemplate, "Rest template can't be null!");

        this.bitbucketRestEndpoint = bitbucketRestEndpoint;
        this.headers = getHttpHeadersWithAuth(username, password);
        this.restTemplate = restTemplate;
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
}
