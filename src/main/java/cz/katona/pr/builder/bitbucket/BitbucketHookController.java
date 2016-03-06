package cz.katona.pr.builder.bitbucket;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import cz.katona.pr.builder.CommentPlanLookup;
import cz.katona.pr.builder.PullRequestBuilderService;
import cz.katona.pr.builder.bitbucket.model.CommentCreated;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BitbucketHookController {

    private static final Logger logger = LoggerFactory.getLogger(BitbucketHookController.class);

    private final String allowedCommentPrefix;
    private final PullRequestBuilderService pullRequestBuilderService;
    private final CommentPlanLookup commentPlanLookup;


    @Autowired
    public BitbucketHookController(@Value("${bitbucket.comment.prefix}") String allowedCommentPrefix,
                                   CommentPlanLookup commentPlanLookup,
                                   PullRequestBuilderService pullRequestBuilderService) {
        this.allowedCommentPrefix = allowedCommentPrefix;
        this.pullRequestBuilderService = pullRequestBuilderService;
        this.commentPlanLookup = commentPlanLookup;
    }

    @RequestMapping(value = "/commentCreated", method = POST)
    public void greeting(@RequestBody CommentCreated commentCreated) {
        String comment = commentCreated.getCommentString();
        logger.debug("Received comment='{}'", comment);

        if (shouldBeProcessed(comment)) {
            pullRequestBuilderService.processComment(commentCreated, commentPlanLookup.getPlanId(comment));
        } else {
            logger.debug("Skipped comment as it doesn't start with {} or wasn't found in the comment-plan lookup",
                    allowedCommentPrefix);
        }
    }

    private boolean shouldBeProcessed(String comment) {
        return comment != null &&
                comment.startsWith(allowedCommentPrefix) &&
                commentPlanLookup.getPlanId(comment) != null;
    }

    @RequestMapping(value = "/info", method = GET)
    public String info() {
        return "Listening";
    }
}
