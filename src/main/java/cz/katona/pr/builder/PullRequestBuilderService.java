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

/**
 * Service that processes comments from pull requests
 */
@Component
public class PullRequestBuilderService {

    private static final Logger logger = LoggerFactory.getLogger(PullRequestBuilderService.class);

    private final BambooService bambooService;

    private final BitbucketService bitbucketService;

    private final CommentService commentService;

    @Autowired
    public PullRequestBuilderService(BambooService bambooService, BitbucketService bitbucketService,
                                     CommentService commentService) {
        notNull(bambooService, "Bamboo service can't be null!");
        notNull(bitbucketService, "Bitbucket service can't be null!");
        notNull(commentService, "Comment service can't be null!");

        this.bambooService = bambooService;
        this.bitbucketService = bitbucketService;
        this.commentService = commentService;
    }

    /**
     * Processes comment from pull request - creates branch build in bamboo if it doesn't exist, enables it,
     * queues up the build and posts a comment with the link to bamboo build as a response to the added comment.
     *
     * @param commentCreated object containing information about created comment in pull request
     * @param associatedPlanId plan id which should be queued up
     */
    public void processComment(CommentCreated commentCreated, String associatedPlanId) {
        notNull(commentCreated, "Created comment can't be null!");
        notEmpty(associatedPlanId, "Plan id can't be empty!");

        String branchName = commentCreated.getSourceBranch();

        logger.info("action=process_comment comment={} status=START",
                commentCreated.getCommentString());

        Branch branch = bambooService.getBranch(associatedPlanId, branchName);
        if (branch == null) {
            logger.debug("action=create_branch status=START");
            branch = bambooService.createBranch(associatedPlanId, branchName);
            logger.debug("action=create_branch status=FINISH");

        } else if (!branch.isEnabled()) {
            logger.debug("action=enable_branch status=START");
            bambooService.enableBranch(branch.getKey());
            logger.debug("action=enable_branch status=FINISH");
        }

        logger.debug("action=queue_job key={} status=START", branch.getKey());
        JobQueued jobQueued = bambooService.queueJob(branch.getKey());
        logger.debug("action=queue_job key={} job_number={} status=FINISH",
                branch.getKey(), jobQueued.getBuildNumber());

        bitbucketService.createComment(commentCreated.getRepository().getFullName(),
                commentCreated.getPullRequest().getId(), commentService.getCommentForBuild(jobQueued),
                commentCreated.getComment().getId());

        logger.info("action=process_comment comment={} status=FINISH",
                commentCreated.getCommentString());
    }
}
