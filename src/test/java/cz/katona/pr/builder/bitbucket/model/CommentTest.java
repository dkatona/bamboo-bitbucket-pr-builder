package cz.katona.pr.builder.bitbucket.model;

import static cz.katona.pr.builder.bitbucket.model.TestUtil.MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class CommentTest {

    @Test
    public void testDeserialization() throws Exception {
        Comment comment = MAPPER.readValue(getClass().getResourceAsStream("/model/comment.json"), Comment.class);
        assertThat(comment.getId(), is(17l));
        assertThat(comment.getParent().getId(), is(16l));
        assertThat(comment.getRawComment(), is("Comment text"));
        assertThat(comment.getInline().getTo(), is(10l));
    }
}
