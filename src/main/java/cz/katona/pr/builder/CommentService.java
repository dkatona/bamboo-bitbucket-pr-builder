package cz.katona.pr.builder;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import cz.katona.pr.builder.bamboo.model.JobQueued;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private static final String BUILD_LINK_PATTERN = "{build_link}";
    private static final String JOKE_PATTERN = "{joke}";
    private static final String JOKE_URI = "http://api.icndb.com/jokes/random";

    private final String commentPattern;
    private final String bambooBrowseUri;

    @Autowired
    public CommentService(@Value("${bitbucket.following.comment.pattern}") String commentPattern,
                          @Value("${bamboo.browse.uri}") String bambooBrowseUri) {
        this.commentPattern = commentPattern;
        this.bambooBrowseUri = bambooBrowseUri;
    }

    public String getCommentForBuild(JobQueued jobQueued) {
        String buildLink = jobQueued.getJobBuildLink(bambooBrowseUri);
        String comment = commentPattern.replaceAll(Pattern.quote(BUILD_LINK_PATTERN), buildLink);
        if (commentPattern.contains(JOKE_PATTERN)) {
            Optional<String> joke = fetchJoke();
            comment = comment.replaceAll(Pattern.quote(JOKE_PATTERN), joke.isPresent() ? joke.get() : "");
        }
        return comment;
    }

    private Optional<String> fetchJoke() {
        String joke = null;
        try {
            HttpResponse<JsonNode> jokeResponse = Unirest.get(JOKE_URI).asJson();
            joke = jokeResponse.getBody().getObject().getJSONObject("value").getString("joke");
        } catch (Exception ex) {
            logger.warn("Cannot fetch joke from {}", JOKE_URI, ex);
        }
        return Optional.ofNullable(joke);
    }
}
