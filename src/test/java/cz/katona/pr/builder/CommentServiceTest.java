package cz.katona.pr.builder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.katona.pr.builder.bamboo.model.JobQueued;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CommentServiceTest {

    private static final String COMMENT_PATTERN_JOKE = "Link to the [build]({build_link}).{joke}";
    private static final String COMMENT_PATTERN_NO_JOKE = "Link to the [build]({build_link}).";

    private static final String BAMBOO_BROWSE_URI = "https://myserver.net/builds/browse";

    private static final JobQueued JOB_QUEUED = new JobQueued("DP", 23, "DP-FBB-3", "manual");

    private static final String JOKE_API_RESPONSE = "{\n" +
            "  \"type\": \"success\",\n" +
            "  \"value\": {\n" +
            "    \"id\": 82,\n" +
            "    \"joke\": \"Joke about Chuck.\",\n" +
            "    \"categories\": []\n" +
            "  }\n" +
            "}";

    @Test
    public void commentForBuildNoJoke() throws Exception {
        CommentService commentService = new CommentService(COMMENT_PATTERN_NO_JOKE, BAMBOO_BROWSE_URI, mockJokeApi(false));

        String comment = commentService.getCommentForBuild(JOB_QUEUED);
        assertThat(comment, is("Link to the [build](https://myserver.net/builds/browse/DP-FBB-3)."));
    }

    @Test
    public void commentForBuildWithJoke() throws Exception {
        CommentService commentService = new CommentService(COMMENT_PATTERN_JOKE, BAMBOO_BROWSE_URI, mockJokeApi(false));

        String comment = commentService.getCommentForBuild(JOB_QUEUED);
        assertThat(comment, is("Link to the [build](https://myserver.net/builds/browse/DP-FBB-3)." +
                "Joke about Chuck."));
    }

    @Test
    public void jokeCannotBeRetrieved() throws Exception {
        CommentService commentService = new CommentService(COMMENT_PATTERN_JOKE, BAMBOO_BROWSE_URI, mockJokeApi(true));

        //if joke API is not accessible it shouldn't prevent the comment to be posted
        String comment = commentService.getCommentForBuild(JOB_QUEUED);
        assertThat(comment, is("Link to the [build](https://myserver.net/builds/browse/DP-FBB-3)."));

    }

    private RestTemplate mockJokeApi(boolean throwException) throws Exception {
        RestTemplate restTemplate = mock(RestTemplate.class);
        if (!throwException) {
            JsonNode jsonNode = new ObjectMapper().readTree(JOKE_API_RESPONSE);
            ResponseEntity<JsonNode> response = ResponseEntity.ok(jsonNode);
            when(restTemplate.getForEntity(CommentService.JOKE_URI, JsonNode.class)).thenReturn(response);
        } else {
            when(restTemplate.getForEntity(CommentService.JOKE_URI, JsonNode.class)).thenThrow(
                    new RuntimeException("Failed to retrieve joke"));
        }
        return restTemplate;
    }
}
