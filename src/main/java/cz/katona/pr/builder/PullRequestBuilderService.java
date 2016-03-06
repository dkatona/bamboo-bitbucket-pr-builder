package cz.katona.pr.builder;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import cz.katona.pr.builder.bamboo.BambooService;
import cz.katona.pr.builder.bamboo.model.Branch;
import cz.katona.pr.builder.bamboo.model.JobQueued;
import cz.katona.pr.builder.bitbucket.BitbucketService;
import cz.katona.pr.builder.bitbucket.model.CommentCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PullRequestBuilderService {

    private static final Logger logger = LoggerFactory.getLogger(PullRequestBuilderService.class);

    private final BambooService bambooService;

    private final BitbucketService bitbucketService;

    private final CommentService commentService;

    @Autowired
    public PullRequestBuilderService(BambooService bambooService, BitbucketService bitbucketService,
                                     CommentService commentService) {
        this.bambooService = bambooService;
        this.bitbucketService = bitbucketService;
        this.commentService = commentService;
    }

    public void processComment(CommentCreated commentCreated, String associatedPlanId) {
        notNull(commentCreated, "Created comment can't be null!");
        notEmpty(associatedPlanId, "Plan id can't be empty!");

        String comment = commentCreated.getCommentString();
        String branchName = commentCreated.getSourceBranch();

        Branch branch = bambooService.getBranch(associatedPlanId, branchName);
        if (branch == null) {
            branch = bambooService.createBranch(associatedPlanId, branchName);
        }
        JobQueued jobQueued = bambooService.queueJob(branch.getKey());
        logger.info("Job queued build nummber - {}", jobQueued.getBuildNumber());

        bitbucketService.createComment(commentCreated.getRepository().getFullName(),
                commentCreated.getPullRequest().getId(), commentService.getCommentForBuild(jobQueued),
                commentCreated.getComment().getId());

    }
}
