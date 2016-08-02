package cz.katona.pr.builder;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import cz.katona.pr.builder.bitbucket.model.CommentCreated;
import cz.katona.pr.builder.util.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for receiving hooks about created comments in pull requests
 */
@RestController
public class PullRequestCommentHookController {

    private static final Logger logger = LoggerFactory.getLogger(PullRequestCommentHookController.class);

    private final String allowedCommentPrefix;
    private final PullRequestBuilderService pullRequestBuilderService;
    private final CommentPlanLookup commentPlanLookup;


    @Autowired
    public PullRequestCommentHookController(@Value("${bitbucket.comment.prefix}") String allowedCommentPrefix,
                                            CommentPlanLookup commentPlanLookup,
                                            PullRequestBuilderService pullRequestBuilderService) {
        notEmpty(allowedCommentPrefix, "Allowed comment prefix can't be empty!");
        notNull(commentPlanLookup, "Comment plan lookup can't be null!");
        notNull(pullRequestBuilderService, "pullRequestBuilderService can't be null!");

        this.allowedCommentPrefix = allowedCommentPrefix;
        this.pullRequestBuilderService = pullRequestBuilderService;
        this.commentPlanLookup = commentPlanLookup;
    }

    /**
     * Called when comment is created in pull request via hooks (need to be set)
     * @param commentCreated created comment
     */
    @RequestMapping(value = "/commentCreated", method = POST)
    public void commentCreated(@RequestBody CommentCreated commentCreated) {
        notNull(commentCreated, "Comment created can't be null!");

        String comment = commentCreated.getCommentString();
        String repositoryName = commentCreated.getRepositoryName();

        try {
            LoggingUtils.initializeLogContext(commentCreated);

            logger.debug("action=process_comment_controller comment={} status=START", comment);
            if (shouldBeProcessed(repositoryName, comment)) {
                pullRequestBuilderService.processComment(commentCreated,
                        commentPlanLookup.getPlanId(repositoryName, comment));
                logger.debug("action=process_comment_controller comment={} status=FINISH", comment);
            } else {
                logger.debug("action=process_comment_controller comment={} status=SKIPPED", comment);
            }
        } finally {
            LoggingUtils.clearLogContext();
        }
    }

    private boolean shouldBeProcessed(String repositoryName, String comment) {
        return comment != null &&
                comment.startsWith(allowedCommentPrefix) &&
                commentPlanLookup.getPlanId(repositoryName, comment) != null;
    }
}
