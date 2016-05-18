package cz.katona.pr.builder.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload for adding a comment to the pull request as specified
 * <a href="https://confluence.atlassian.com/bitbucket/pullrequests-resource-1-0-296095210.html#pullrequestsResource1.0-POSTanewcomment">here</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentAdd {

    @JsonProperty("parent_id")
    private Long parentId;
    private String content;

    /**
     * Creates new payload for adding a comment
     * @param content content of the comment
     * @param parentId id of the parent comment (if adding a reply)
     */
    @JsonCreator
    public CommentAdd(@JsonProperty("content") String content,
                      @JsonProperty("parent_id") Long parentId) {
        this.parentId = parentId;
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getContent() {
        return content;
    }
}
