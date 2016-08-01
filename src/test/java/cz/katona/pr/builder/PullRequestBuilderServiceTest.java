package cz.katona.pr.builder;

import static cz.katona.pr.builder.TestUtil.MAPPER;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cz.katona.pr.builder.bamboo.BambooService;
import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bamboo.model.JobQueued;
import cz.katona.pr.builder.bitbucket.BitbucketService;
import cz.katona.pr.builder.bitbucket.model.CommentCreated;
import org.junit.Before;
import org.junit.Test;

public class PullRequestBuilderServiceTest {


    private PullRequestBuilderService pullRequestBuilderService;
    private BambooService bambooService;
    private BitbucketService bitbucketService;
    private CommentService commentService;
    private CommentCreated commentCreated;

    private static final String PLAN_ID = "DB-324";
    private static final String BRANCH = "branch2";
    private static final String BRANCH_KEY = "DP-FBB395";

    private static final JobQueued JOB_QUEUED = new JobQueued(PLAN_ID, 23, PLAN_ID + "23", "Manual");


    @Before
    public void setUp() throws Exception {
        bambooService = mock(BambooService.class);
        bitbucketService = mock(BitbucketService.class);
        commentService = mock(CommentService.class);
        pullRequestBuilderService = new PullRequestBuilderService(bambooService, bitbucketService, commentService);

        commentCreated = MAPPER.readValue(getClass().getResourceAsStream("/bitbucket/model/commentCreated.json"),
                CommentCreated.class);
    }

    @Test
    public void testFlowWithCreateBranch() throws Exception {
        when(bambooService.getBranch(PLAN_ID, BRANCH)).thenReturn(null);
        when(bambooService.createBranch(PLAN_ID, BRANCH)).thenReturn(new Branch(BRANCH, BRANCH, BRANCH_KEY, true));

        mockJobQueued();

        pullRequestBuilderService.processComment(commentCreated, PLAN_ID);
        verify(bambooService).createBranch(PLAN_ID, BRANCH);

        verifyQueuedAndComment();

    }

    @Test
    public void testFlowWithEnableBranch() {
        Branch branch = new Branch(BRANCH, BRANCH, BRANCH_KEY, false);
        when(bambooService.getBranch(PLAN_ID, BRANCH)).thenReturn(branch);

        mockJobQueued();

        pullRequestBuilderService.processComment(commentCreated, PLAN_ID);
        verify(bambooService).enableBranch(BRANCH_KEY);
        verify(bambooService, times(0)).createBranch(anyString(), anyString());

        verifyQueuedAndComment();
    }

    private void mockJobQueued() {
        when(bambooService.queueJob(BRANCH_KEY)).thenReturn(JOB_QUEUED);
        when(commentService.getCommentForBuild(JOB_QUEUED)).thenReturn("Comment");
    }

    private void verifyQueuedAndComment() {
        verify(bambooService).queueJob(BRANCH_KEY);
        verify(commentService).getCommentForBuild(JOB_QUEUED);
        verify(bitbucketService).createComment("team_name/repo_name", 1l, "Comment", 17l);
    }
}
