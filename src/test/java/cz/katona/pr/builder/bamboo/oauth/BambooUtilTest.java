package cz.katona.pr.builder.bamboo.oauth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class BambooUtilTest {

    @Test
    public void testReadPrivateKey() throws Exception {
        String privateKey = BambooUtil.readPrivateKey("classpath:private_key.pem");
        assertThat(privateKey, not(containsString("PRIVATE KEY")));
    }
}
