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
@TestPropertySource(properties = {"bitbucket.basic.user=abc", "bitbucket.basic.password=def",
        "bamboo.basic.user=gef", "bamboo.basic.password=klm"})
public class SpringBasicAuthContextTest {

    @Autowired
    private PullRequestBuilderService pullRequestBuilderService;

    @Test
    public void loadContext() throws Exception {
        assertThat(pullRequestBuilderService, notNullValue());
    }
}
