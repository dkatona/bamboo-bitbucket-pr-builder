package cz.katona.pr.builder.bitbucket.model;

import static cz.katona.pr.builder.bitbucket.model.TestUtil.MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class UserTest {

    @Test
    public void testDeserialization() throws Exception {
        User user = MAPPER.readValue(getClass().getResourceAsStream("/model/user.json"), User.class);
        assertThat(user.getUsername(), is("emmap1"));
        assertThat(user.getDisplayName(), is("Emma"));
        assertThat(user.getUuid(), is("{a54f16da-24e9-4d7f-a3a7-b1ba2cd98aa3}"));
    }
}
