package cz.katona.pr.builder.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentAdd {

    @JsonProperty("parent_id")
    private Long parentId;
    private String content;

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
