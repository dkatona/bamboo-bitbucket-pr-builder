package cz.katona.pr.builder.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload for a created comment as specified <a href="https://confluence.atlassian.com/bitbucket/event-payloads-740262817.html#EventPayloads-CommentCreated.1">here</a>.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentCreated {

    private User actor;

    private Repository repository;

    private PullRequest pullRequest;

    private Comment comment;

    @JsonCreator
    public CommentCreated(@JsonProperty("actor") User actor,
                          @JsonProperty("repository") Repository repository,
                          @JsonProperty("pullrequest") PullRequest pullRequest,
                          @JsonProperty("comment") Comment comment) {
        this.actor = actor;
        this.repository = repository;
        this.pullRequest = pullRequest;
        this.comment = comment;
    }

    public User getActor() {
        return actor;
    }

    public Repository getRepository() {
        return repository;
    }

    public PullRequest getPullRequest() {
        return pullRequest;
    }

    public Comment getComment() {
        return comment;
    }

    public String getCommentString(){
        return getComment().getRawComment();
    }

    public String getSourceBranch() {
        return getPullRequest().getSourceBranch();
    }

    public String getDestinationBranch() {
        return getPullRequest().getDestinationBranch();
    }

    public String getRepositoryName() {
        return getRepository().getName();
    }

}
