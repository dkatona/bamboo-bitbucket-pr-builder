package cz.katona.pr.builder.bamboo.oauth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class BambooOAuthSettingsTest {

    private static final Resource PRIVATE_KEY = new ClassPathResource("private_key.pem");

    private static final BambooOAuthSettings OAUTH_SETTINGS = new BambooOAuthSettings(
            "apiKey", PRIVATE_KEY, "token", "tokenSecret", new BambooOAuthApi("https://my.bamboo.com", PRIVATE_KEY));

    @Test
    public void testAccessToken() throws Exception {
        assertThat(OAUTH_SETTINGS.getAccessToken().getToken(), is("token"));
        assertThat(OAUTH_SETTINGS.getAccessToken().getTokenSecret(), is("tokenSecret"));
    }

    @Test
    public void testService() throws Exception {
        assertThat(OAUTH_SETTINGS.getOAuthService().getConfig().getApiKey(), is("apiKey"));
    }
}
