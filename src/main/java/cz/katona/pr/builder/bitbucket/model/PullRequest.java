package cz.katona.pr.builder.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Payload for pull request as specified <a href="https://confluence.atlassian.com/bitbucket/event-payloads-740262817.html#EventPayloads-entity_pullrequestPullRequest">here</a>.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {

    private Long id;
    private String title;
    private String description;
    private PullRequestState state;
    private User author;
    private CodeInfo source;
    private CodeInfo destination;
    private Commit mergeCommit;
    private List<User> participants;
    private List<User> reviewers;
    private boolean closeSourceBranch;
    private User closedBy;
    private String declineReason;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;

    @JsonCreator
    public PullRequest(
            @JsonProperty("id") Long id,
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("author") User author,
            @JsonProperty("state") PullRequestState state,
            @JsonProperty("source") CodeInfo source,
            @JsonProperty("destination") CodeInfo destination,
            @JsonProperty("merge_commit") Commit mergeCommit,
            @JsonProperty("participants") List<User> participants,
            @JsonProperty("reviewers") List<User> reviewers,
            @JsonProperty("close_source_branch") boolean closeSourceBranch,
            @JsonProperty("closed_by") User closedBy,
            @JsonProperty("reason") String declineReason,
             @JsonProperty("created_on") ZonedDateTime createdOn,
             @JsonProperty("updated_on") ZonedDateTime updatedOn) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.state = state;
        this.source = source;
        this.destination = destination;
        this.mergeCommit = mergeCommit;
        this.participants = participants;
        this.reviewers = reviewers;
        this.closeSourceBranch = closeSourceBranch;
        this.closedBy = closedBy;
        this.declineReason = declineReason;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public PullRequestState getState() {
        return state;
    }

    public CodeInfo getSource() {
        return source;
    }

    public String getSourceBranch() {
        return getSource().getBranch().getName();
    }

    public CodeInfo getDestination() {
        return destination;
    }

    public String getDestinationBranch() {
        return getDestination().getBranch().getName();
    }

    public Commit getMergeCommit() {
        return mergeCommit;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public List<User> getReviewers() {
        return reviewers;
    }

    public boolean isCloseSourceBranch() {
        return closeSourceBranch;
    }

    public User getClosedBy() {
        return closedBy;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public ZonedDateTime getUpdatedOn() {
        return updatedOn;
    }

    public User getAuthor() {
        return author;
    }

    public static class CodeInfo {
        private Branch branch;
        private Commit commit;
        private Repository repository;

        public CodeInfo(@JsonProperty("branch") Branch branch,
                        @JsonProperty("commit") Commit commit,
                        @JsonProperty("repository") Repository repository) {
            this.branch = branch;
            this.commit = commit;
            this.repository = repository;
        }

        public Branch getBranch() {
            return branch;
        }

        public Commit getCommit() {
            return commit;
        }

        public Repository getRepository() {
            return repository;
        }
    }

    public static class Branch {
        private String name;

        public Branch(@JsonProperty("name") String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static class Commit {
        private String hash;

        public Commit(@JsonProperty("hash") String hash) {
            this.hash = hash;
        }

        public String getHash() {
            return hash;
        }
    }

    public enum PullRequestState {
        OPEN, MERGED, DECLINED
    }


}
