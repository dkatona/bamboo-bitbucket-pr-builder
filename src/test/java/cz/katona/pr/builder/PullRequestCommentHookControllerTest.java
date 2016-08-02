package cz.katona.pr.builder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import cz.katona.pr.builder.bitbucket.model.CommentCreated;
import cz.katona.pr.builder.bitbucket.model.PullRequest;
import org.junit.Before;
import org.junit.Test;

public class PullRequestCommentHookControllerTest {

    private PullRequestCommentHookController controller;
    private CommentPlanLookup commentPlanLookup;
    private PullRequestBuilderService pullRequestBuilderService;

    @Before
    public void setUp() throws Exception {
        commentPlanLookup = mock(CommentPlanLookup.class);
        pullRequestBuilderService = mock(PullRequestBuilderService.class);

        controller = new PullRequestCommentHookController("bamboo:",commentPlanLookup, pullRequestBuilderService);

    }

    @Test
    public void testProcessComment() throws Exception {
        CommentCreated commentCreated = mockCommentCreated("bamboo:build");
        when(commentPlanLookup.getPlanId("data-platform", "bamboo:build")).thenReturn("DP-123");

        controller.commentCreated(commentCreated);

        verify(pullRequestBuilderService).processComment(commentCreated, "DP-123");

    }

    @Test
    public void testProcessCommentBadPrefix() throws Exception {
        CommentCreated commentCreated = mockCommentCreated("build");
        controller.commentCreated(commentCreated);
        verifyZeroInteractions(pullRequestBuilderService);
        verifyZeroInteractions(commentPlanLookup);
    }

    @Test
    public void testProcessCommentNotInLookup() throws Exception {
        CommentCreated commentCreated = mockCommentCreated("bamboo:build");
        when(commentPlanLookup.getPlanId("data-platform", "bamboo:build")).thenReturn(null);
        controller.commentCreated(commentCreated);
        verifyZeroInteractions(pullRequestBuilderService);
    }

    private CommentCreated mockCommentCreated(String comment) {
        CommentCreated commentCreated = mock(CommentCreated.class);
        PullRequest pullRequest = mock(PullRequest.class);
        when(pullRequest.getId()).thenReturn(123l);
        when(commentCreated.getPullRequest()).thenReturn(pullRequest);
        when(commentCreated.getCommentString()).thenReturn(comment);
        when(commentCreated.getRepositoryName()).thenReturn("data-platform");
        when(commentCreated.getSourceBranch()).thenReturn("dk-abc");
        return commentCreated;
    }
}
