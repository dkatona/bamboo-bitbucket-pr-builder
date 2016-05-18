package cz.katona.pr.builder.bitbucket.oauth;

import static cz.katona.pr.builder.bitbucket.oauth.BitbucketOAuthApi.BITBUCKET_AUTHORIZE_ENDPOINT;
import static cz.katona.pr.builder.bitbucket.oauth.BitbucketOAuthApi.BITBUCKET_OAUTH_ENDPOINT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.github.scribejava.core.model.OAuthConfig;
import org.junit.Test;

public class BitbucketOAuthApiTest {

    private static final BitbucketOAuthApi OAUTH_API = new BitbucketOAuthApi();

    @Test
    public void testAuthorizationUrl() throws Exception {
        OAuthConfig config = new OAuthConfig("abc", "def");
        String authorizationUrl = OAUTH_API.getAuthorizationUrl(config);
        assertThat(authorizationUrl, is(BITBUCKET_AUTHORIZE_ENDPOINT + "?client_id=abc&response_type=token"));
    }

    @Test
    public void testAccessTokenEndpoint() throws Exception {
           assertThat(OAUTH_API.getAccessTokenEndpoint(), is(BITBUCKET_OAUTH_ENDPOINT));
    }
}
