package cz.katona.pr.builder.bitbucket.oauth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.junit.Test;

public class BitbucketOAuthSettingsTest {

    private static final BitbucketOAuthSettings SETTINGS = new BitbucketOAuthSettings("abc", "def");

    @Test
    public void testOAuthServiceConfiguration() throws Exception {
        OAuth20Service oAuthService = SETTINGS.getOAuthService();
        OAuthConfig config = oAuthService.getConfig();
        assertThat(config.getApiKey(), is("abc"));
        assertThat(config.getApiSecret(), is("def"));
        assertThat(config.getGrantType(), is("client_credentials"));
    }

    @Test
    public void testOAuthRequestConfiguration() throws Exception {
        OAuthRequest oAuthRequest = SETTINGS.buildOAuthRequest();

        //redirect_uri=oob is added by framework itself
        assertThat(oAuthRequest.getBodyContents(),
                is("client_id=abc&client_secret=def&redirect_uri=oob&grant_type=client_credentials"));

    }
}
