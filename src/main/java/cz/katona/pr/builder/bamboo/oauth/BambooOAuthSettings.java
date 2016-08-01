package cz.katona.pr.builder.bamboo.oauth;

import static cz.katona.pr.builder.bamboo.oauth.BambooUtil.readPrivateKey;
import static org.apache.commons.lang3.Validate.*;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import cz.katona.pr.builder.bamboo.BambooException;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Settings related to bamboo OAuth1a endpoint, this bean is enabled only if these properties are set
 * <ul>
 *     <li>bamboo.oauth.privateKey</li>
 *     <li>bamboo.oauth.apiKey</li>
 *     <li>bamboo.oauth.accessToken</li>
 *     <li>bamboo.oauth.accessTokenSecret</li>
 * </ul>
 */
@Component
@ConditionalOnProperty(name = {"privateKey", "apiKey", "accessToken", "accessTokenSecret"}, prefix = "bamboo.oauth")
class BambooOAuthSettings {

    private final OAuth10aService service;
    private final OAuth1AccessToken oAuth1AccessToken;

    @Autowired
    public BambooOAuthSettings(@Value("${bamboo.oauth.apiKey}") String apiKey,
                               @Value("${bamboo.oauth.privateKey}") String privateKeyPath,
                               @Value("${bamboo.oauth.accessToken}") String accessToken,
                               @Value("${bamboo.oauth.accessTokenSecret}") String accessTokenSecret,
                               BambooOAuthApi bambooOAuthApi) {
        notEmpty(apiKey, "Api key can't be empty!");
        notEmpty(privateKeyPath, "Private key can't be null!");
        notEmpty(accessToken, "Access token can't be empty!");
        notEmpty(accessTokenSecret, "Access token secret can't be empty!");

        this.service = new ServiceBuilder().apiKey(apiKey).apiSecret(
                readPrivateKey(privateKeyPath)).build(bambooOAuthApi);
        this.oAuth1AccessToken = new OAuth1AccessToken(accessToken, accessTokenSecret);
    }

    public OAuth1AccessToken getAccessToken() {
        return oAuth1AccessToken;
    }

    public OAuth10aService getOAuthService() {
        return service;
    }
}
