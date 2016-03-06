package cz.katona.pr.builder.bitbucket.model;

import static cz.katona.pr.builder.bitbucket.model.TestUtil.MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class RepositoryTest {

    @Test
    public void testDeserialization() throws Exception {
        Repository repository = MAPPER.readValue(getClass().getResourceAsStream("/bitbucket/model/repository.json"),
                Repository.class);
        assertThat(repository.getName(), is("repo_name"));
        assertThat(repository.getFullName(), is("team_name/repo_name"));
        assertThat(repository.getUuid(), is("{673a6070-3421-46c9-9d48-90745f7bfe8e}"));
        assertThat(repository.getWebsite(), is("https://mywebsite.com/"));
        assertThat(repository.getScm(), is("git"));
        assertThat(repository.isPrivate(), is(Boolean.TRUE));

    }
}
