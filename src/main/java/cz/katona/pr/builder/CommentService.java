package cz.katona.pr.builder;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import com.fasterxml.jackson.databind.JsonNode;
import cz.katona.pr.builder.bamboo.model.JobQueued;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service that generates comments for given build. Requires following settings:
 * <ul>
 *     <li>bitbucket.following.comment.pattern - pattern for the comment, {build_link} or {joke} can be used inside</li>
 *     <li>bamboo.browse.uri - uri to Bamboo for browsing </li>
 * </ul>
 */
@Component
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private static final String BUILD_LINK_PATTERN = "{build_link}";
    private static final String JOKE_PATTERN = "{joke}";

    static final String JOKE_URI = "http://api.icndb.com/jokes/random";

    private final String commentPattern;
    private final String bambooBrowseUri;
    private final RestTemplate restTemplate;

    @Autowired
    public CommentService(@Value("${bitbucket.following.comment.pattern}") String commentPattern,
                          @Value("${bamboo.browse.uri}") String bambooBrowseUri,
                          RestTemplate restTemplate) {
        notEmpty(commentPattern, "Comment pattern can't be empty!");
        notEmpty(bambooBrowseUri, "Bamboo browse uri can't be empty!");
        notNull(restTemplate, "Rest template can't be null!");

        this.commentPattern = commentPattern;
        this.bambooBrowseUri = bambooBrowseUri;
        this.restTemplate = restTemplate;
    }

    /**
     * Returns comment for the queued job
     * @param jobQueued job queued in bamboo
     * @return comment about the build
     */
    public String getCommentForBuild(JobQueued jobQueued) {
        notNull(jobQueued, "Job queued can't be null!");

        String buildLink = jobQueued.getJobBuildLink(bambooBrowseUri);
        String comment = commentPattern.replaceAll(Pattern.quote(BUILD_LINK_PATTERN), buildLink);
        if (commentPattern.contains(JOKE_PATTERN)) {
            Optional<String> joke = fetchJoke();
            comment = comment.replaceAll(Pattern.quote(JOKE_PATTERN), joke.isPresent() ? joke.get() : "");
        }
        return comment;
    }

     Optional<String> fetchJoke() {
        String joke = null;
        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(JOKE_URI, JsonNode.class);
            joke = response.getBody().get("value").get("joke").asText();
        } catch (Exception ex) {
            logger.warn("Cannot fetch joke from {}", JOKE_URI, ex);
        }
        return Optional.ofNullable(joke);
    }
}
