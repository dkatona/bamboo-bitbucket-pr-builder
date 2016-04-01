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

@Component
@ConditionalOnProperty(name = {"clientId", "clientSecret"}, prefix = "bitbucket.oauth")
class OAuthSettings {

    private final OAuth20Service service;

    @Autowired
    public OAuthSettings(@Value("${bitbucket.oauth.clientId}") String clientId,
                         @Value("${bitbucket.oauth.clientSecret}") String clientSecret) {

        this.service = new ServiceBuilder().apiKey(clientId).apiSecret(clientSecret).grantType("client_credentials")
                .build(new BitbucketOAuthApi());
    }

    public OAuth20Service getOAuthService() {
        return service;
    }

    public OAuth2AccessToken getAccessToken() {
        OAuthRequest request = new OAuthRequest(service.getApi().getAccessTokenVerb(), service.getApi().getAccessTokenEndpoint(), service);
        OAuthConfig config = service.getConfig();
        request.addParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
        request.addParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
        request.addParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
        request.addParameter(OAuthConstants.GRANT_TYPE, config.getGrantType());
        return service.getApi().getAccessTokenExtractor().extract(request.send().getBody());
    }
}
