package cz.katona.pr.builder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

public class CommentPlanLookupTest {

    @Test
    public void testGetPlanId() throws Exception {
        MockEnvironment mockEnvironment = new MockEnvironment()
                .withProperty("comment.my-repository.test.planId", "DP-FBB")
                .withProperty("comment.my-repository.build.planId", "DP-BUILD")
                .withProperty("comment.other-repository.test.planId", "DP-FAA");
        CommentPlanLookup commentPlanLookup = new CommentPlanLookup("bamboo:", mockEnvironment);
        assertThat(commentPlanLookup.getPlanId("my-repository", "bamboo:build"), is("DP-BUILD"));
        assertThat(commentPlanLookup.getPlanId("my-repository", "bamboo:test"), is("DP-FBB"));
        assertThat(commentPlanLookup.getPlanId("other-repository", "bamboo:test"), is("DP-FAA"));
        assertThat(commentPlanLookup.getPlanId("old-repository", "bamboo:test"), is(nullValue()));
        assertThat(commentPlanLookup.getPlanId("my-repository", "test"), is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCommentConfiguration() throws Exception {
        MockEnvironment mockEnvironment = new MockEnvironment()
                .withProperty("comment.my-repository.test.planId", "DP-FBB")
                .withProperty("comment.test.planId", "DP-FAA");
        CommentPlanLookup commentPlanLookup = new CommentPlanLookup("bamboo:", mockEnvironment);
        commentPlanLookup.getPlanId("my-repository", "bamboo:build");
    }

    @Test(expected = IllegalArgumentException.class)
    public void repositoryNameIsRequired() throws Exception {
        MockEnvironment mockEnvironment = new MockEnvironment()
                .withProperty("comment.my-repository.test.planId", "DP-FBB");
        CommentPlanLookup commentPlanLookup = new CommentPlanLookup("bamboo:", mockEnvironment);
        commentPlanLookup.getPlanId("", "bamboo:build");
    }

    @Test(expected = IllegalArgumentException.class)
    public void commentIsRequired() throws Exception {
        MockEnvironment mockEnvironment = new MockEnvironment()
                .withProperty("comment.my-repository.test.planId", "DP-FBB");
        CommentPlanLookup commentPlanLookup = new CommentPlanLookup("bamboo:", mockEnvironment);
        commentPlanLookup.getPlanId("my-repository", "");

    }
}
