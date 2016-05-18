package cz.katona.pr.builder.bitbucket.model;

import static cz.katona.pr.builder.TestUtil.MAPPER;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class CommentAddTest {

    @Test
    public void testDeserialization() throws Exception {
        CommentAdd commentAdd = MAPPER.readValue(getClass().getResourceAsStream("/bitbucket/model/commentAdd.json"),
                CommentAdd.class);
        assertThat(commentAdd.getParentId(), is(123l));
        assertThat(commentAdd.getContent(), is("New comment"));
    }

    @Test
    public void testSerialization() throws Exception {
        CommentAdd commentAdd = new CommentAdd("Another comment", 124l);
        String json = MAPPER.writeValueAsString(commentAdd);
        assertThatJson(json).node("content").isStringEqualTo("Another comment");
        assertThatJson(json).node("parent_id").isEqualTo(124);
    }
}
