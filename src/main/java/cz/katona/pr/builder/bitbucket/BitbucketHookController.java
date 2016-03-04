package cz.katona.pr.builder.bitbucket;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import cz.katona.pr.builder.bitbucket.model.CommentCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BitbucketHookController {

    private static final Logger logger = LoggerFactory.getLogger(BitbucketHookController.class);

    @RequestMapping(value = "/commentCreated", method = POST)
    public void greeting(@RequestBody CommentCreated commentCreated) {
        logger.info("received the comment");
        logger.info("comment {}", commentCreated.getCommentString());

    }

    @RequestMapping(value = "/info", method = GET)
    public String info() {
        return "Listening";
    }
}
