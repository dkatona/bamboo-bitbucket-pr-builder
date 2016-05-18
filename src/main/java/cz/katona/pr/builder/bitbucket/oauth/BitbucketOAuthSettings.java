package cz.katona.pr.builder.bitbucket.oauth;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Settings related to bitbucket OAuth endpoint, this bean is enabled only if these properties are set
 * <ul>
 *     <li>bitbucket.oauth.clientId</li>
 *     <li>bitbucket.oauth.clientSecret</li>
 * </ul>
 */
@Component
@ConditionalOnProperty(name = {"clientId", "clientSecret"}, prefix = "bitbucket.oauth")
class BitbucketOAuthSettings {

    private final OAuth20Service service;

    @Autowired
    public BitbucketOAuthSettings(@Value("${bitbucket.oauth.clientId}") String clientId,
                                  @Value("${bitbucket.oauth.clientSecret}") String clientSecret) {

        this.service = new ServiceBuilder().apiKey(clientId).apiSecret(clientSecret).grantType("client_credentials")
                .build(new BitbucketOAuthApi());
    }

    OAuth20Service getOAuthService() {
        return service;
    }

    OAuth2AccessToken getAccessToken() {
        OAuthRequest request = buildOAuthRequest();
        return service.getApi().getAccessTokenExtractor().extract(request.send().getBody());
    }

    OAuthRequest buildOAuthRequest() {
        OAuthRequest request = new OAuthRequest(service.getApi().getAccessTokenVerb(), service.getApi().getAccessTokenEndpoint(), service);
        OAuthConfig config = service.getConfig();
        request.addParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
        request.addParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
        request.addParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
        request.addParameter(OAuthConstants.GRANT_TYPE, config.getGrantType());
        return request;
    }
}
