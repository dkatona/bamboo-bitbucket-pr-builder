package cz.katona.pr.builder.bitbucket.model;

import static cz.katona.pr.builder.bitbucket.model.TestUtil.MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class CommentCreatedTest {

    @Test
    public void testDeserialization() throws Exception {
        CommentCreated commentCreated = MAPPER.readValue(getClass().getResourceAsStream("/model/commentCreated.json"),
                CommentCreated.class);
        assertThat(commentCreated.getCommentString(), is("Comment text"));
        assertThat(commentCreated.getSourceBranch(), is("branch2"));
        assertThat(commentCreated.getDestinationBranch(), is("master"));
        assertThat(commentCreated.getRepositoryName(), is("repo_name"));
    }
}
