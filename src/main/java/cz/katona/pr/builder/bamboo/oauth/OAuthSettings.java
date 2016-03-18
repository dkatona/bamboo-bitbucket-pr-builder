package cz.katona.pr.builder.bamboo.oauth;

import static cz.katona.pr.builder.bamboo.oauth.BambooUtil.readPrivateKey;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = {"privateKey", "apiKey", "accessToken", "accessTokenSecret"}, prefix = "bamboo.oauth")
class OAuthSettings {

    private final OAuth10aService service;
    private final OAuth1AccessToken oAuth1AccessToken;

    @Autowired
    public OAuthSettings(@Value("${bamboo.oauth.apiKey}") String apiKey,
                         @Value("file://${bamboo.oauth.privateKey}") Resource privateKeyResource,
                         @Value("${bamboo.oauth.accessToken}") String accessToken,
                         @Value("${bamboo.oauth.accessTokenSecret}") String accessTokenSecret,
                         BambooOAuthApi bambooOAuthApi) {

        this.service = new ServiceBuilder().apiKey(apiKey).apiSecret(
                readPrivateKey(privateKeyResource)).build(bambooOAuthApi);
        this.oAuth1AccessToken = new OAuth1AccessToken(accessToken, accessTokenSecret);
    }

    public OAuth1AccessToken getAccessToken() {
        return oAuth1AccessToken;
    }

    public OAuth10aService getOAuthService() {
        return service;
    }
}
