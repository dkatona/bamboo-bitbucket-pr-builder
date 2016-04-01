package cz.katona.pr.builder.bitbucket;

import static cz.katona.pr.builder.bamboo.BambooResources.BRANCH_CREATE_RESOURCE;
import static cz.katona.pr.builder.bitbucket.BitbucketResources.COMMENTS_RESOURCE;
import static cz.katona.pr.builder.util.RequestUtils.*;

import cz.katona.pr.builder.bamboo.BambooException;
import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bitbucket.model.CommentAdd;
import cz.katona.pr.builder.util.RequestUtils;
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

@Component
@ConditionalOnProperty(name = {"user", "password"}, prefix = "bitbucket.basic")
public class BitbucketBasicAuthService implements BitbucketService {

    //headers which contain basic auth
    private final HttpHeaders headers;

    private final String bitbucketRestEndpoint;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public BitbucketBasicAuthService(@Value("${bitbucket.rest.uri}") String bitbucketRestEndpoint,
                                     @Value("${bitbucket.basic.user}") String username,
                                     @Value("${bitbucket.basic.password}") String password) {
        this.bitbucketRestEndpoint = bitbucketRestEndpoint;
        this.headers = getHttpHeadersWithAuth(username, password);
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
