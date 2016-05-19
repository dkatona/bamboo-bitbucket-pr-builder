package cz.katona.pr.builder.bamboo.oauth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.services.SignatureService;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class BambooOAuthApiTest {

    private static final BambooOAuthApi OAUTH_API = new BambooOAuthApi("https://my.bamboo.com",
            new ClassPathResource("private_key.pem"));

    @Test
    public void testAccessTokenEndpoint() throws Exception {
        assertThat(OAUTH_API.getAccessTokenEndpoint(), is("https://my.bamboo.com/plugins/servlet/oauth/access-token"));

    }

    @Test
    public void testAuthorizationUrl() throws Exception {
        OAuth1RequestToken requestToken = new OAuth1RequestToken("abc", "def");
        assertThat(OAUTH_API.getAuthorizationUrl(requestToken),
                is("https://my.bamboo.com/plugins/servlet/oauth/authorize?oauth_token=abc"));
    }

    @Test
    public void testRequestTokenEndpoint() throws Exception {
        assertThat(OAUTH_API.getRequestTokenEndpoint(), is("https://my.bamboo.com/plugins/servlet/oauth/request-token"));
    }

    @Test
    public void testSignatureService() throws Exception {
        SignatureService signatureService = OAUTH_API.getSignatureService();
        assertThat(signatureService.getSignatureMethod(), is("RSA-SHA1"));
    }
}
