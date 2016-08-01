package cz.katona.pr.builder;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@TestPropertySource(properties = {"bitbucket.oauth.clientId=abc", "bitbucket.oauth.clientSecret=def",
"bamboo.oauth.privateKey=classpath:private_key.pem", "bamboo.oauth.apiKey=klm", "bamboo.oauth.accessToken=accessToken",
        "bamboo.oauth.accessTokenSecret=tokenSecret"})
public class SpringOAuthContextTest {

    @Autowired
    private PullRequestBuilderService pullRequestBuilderService;

    @Test
    public void loadContext() throws Exception {
        assertThat(pullRequestBuilderService, notNullValue());
    }
}
