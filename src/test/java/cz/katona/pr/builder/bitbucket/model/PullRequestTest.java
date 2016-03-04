package cz.katona.pr.builder.bitbucket.model;

import static cz.katona.pr.builder.bitbucket.model.TestUtil.MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class PullRequestTest {

    @Test
    public void testDeserialization() throws Exception {
        PullRequest pullRequest = MAPPER.readValue(getClass().getResourceAsStream("/model/pullRequest.json"),
                PullRequest.class);
        assertThat(pullRequest.getId(), is(1l));
        assertThat(pullRequest.getTitle(), is("Title of pull request"));
        assertThat(pullRequest.getState(), is(PullRequest.PullRequestState.OPEN));
        assertThat(pullRequest.getSourceBranch(), is("branch2"));
        assertThat(pullRequest.getDestinationBranch(), is("master"));
        assertThat(pullRequest.getAuthor().getUsername(), is("emmap1"));
    }
}
