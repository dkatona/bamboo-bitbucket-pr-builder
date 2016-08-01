package cz.katona.pr.builder.bamboo.oauth;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.services.RSASha1SignatureService;
import com.github.scribejava.core.services.SignatureService;
import cz.katona.pr.builder.bamboo.BambooException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.util.UriTemplate;

import java.io.FileNotFoundException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * Definition for bamboo OAuth1a API. This bean is enabled only if this property is set:
 * <ul>
 *     <li>bamboo.oauth.privateKey</li>
 * </ul>
 */
@Component
@ConditionalOnProperty(name = "privateKey", prefix = "bamboo.oauth")
class BambooOAuthApi extends DefaultApi10a {

    private static final UriTemplate AUTHORIZE_URL_TEMPLATE =
            new UriTemplate("{serverBaseUrl}/plugins/servlet/oauth/authorize?oauth_token={oauth_token}");

    private static final UriTemplate REQUEST_TOKEN_TEMPLATE =
            new UriTemplate("{serverBaseUrl}/plugins/servlet/oauth/request-token");

    private static final UriTemplate ACCESS_TOKEN_TEMPLATE =
            new UriTemplate("{serverBaseUrl}/plugins/servlet/oauth/access-token");

    private String serverBaseUrl = null;

    private String privateKeyPath = null;

    @Autowired
    public BambooOAuthApi(@Value("${bamboo.base.uri}") String serverBaseUrl,
                          @Value("${bamboo.oauth.privateKey}") String privateKeyPath) {
        notEmpty(serverBaseUrl, "Bamboo base uri can't be empty!");
        notEmpty(privateKeyPath, "Private key path can't be empty!");
        this.serverBaseUrl = serverBaseUrl;
        this.privateKeyPath = privateKeyPath;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_TEMPLATE.expand(serverBaseUrl).toString();
    }

    @Override
    public String getAuthorizationUrl(OAuth1RequestToken oAuth1RequestToken) {
        return AUTHORIZE_URL_TEMPLATE.expand(serverBaseUrl, oAuth1RequestToken.getToken()).toString();
    }

    @Override
    public String getRequestTokenEndpoint() {
        return REQUEST_TOKEN_TEMPLATE.expand(serverBaseUrl).toString();
    }

    @Override
    public SignatureService getSignatureService() {
        try {
            String privateKeyString = BambooUtil.readPrivateKey(privateKeyPath);
            KeyFactory fac = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
            PrivateKey privateKey = fac.generatePrivate(privKeySpec);
            return new RSASha1SignatureService(privateKey);
        } catch (Exception e) {
            throw new BambooException("Unable to initialize signature service with given private key!", e);
        }
    }

}
