package cz.katona.pr.builder.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Comment payload as specified <a href="https://confluence.atlassian.com/bitbucket/event-payloads-740262817.html#EventPayloads-entity_commentComment">here</a>.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    private Long id;

    private Comment parent;

    private Content content;

    private Inline inline;

    private ZonedDateTime createdOn;

    private ZonedDateTime updatedOn;

    @JsonCreator
    public Comment(@JsonProperty("id") Long id,
                   @JsonProperty("parent") Comment parent,
                   @JsonProperty("content") Content content,
                   @JsonProperty("inline") Inline inline,
                   @JsonProperty("created_on") ZonedDateTime createdOn,
                   @JsonProperty("updated_on") ZonedDateTime updatedOn) {
        this.id = id;
        this.parent = parent;
        this.content = content;
        this.inline = inline;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public Long getId() {
        return id;
    }

    public Comment getParent() {
        return parent;
    }

    public Content getContent() {
        return content;
    }

    public String getRawComment(){
        return getContent().getRaw();
    }

    public Inline getInline() {
        return inline;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public ZonedDateTime getUpdatedOn() {
        return updatedOn;
    }

    public static class Content {
        private String raw;
        private String html;
        private String markup;

        public Content(@JsonProperty("raw") String raw,
                       @JsonProperty("html") String html,
                       @JsonProperty("markup") String markup) {
            this.raw = raw;
            this.html = html;
            this.markup = markup;
        }

        public String getRaw() {
            return raw;
        }

        public String getHtml() {
            return html;
        }

        public String getMarkup() {
            return markup;
        }
    }

    public static class Inline {
        private String path;
        private Long from;
        private Long to;

        public Inline(@JsonProperty("path") String path,
                      @JsonProperty("from") Long from,
                      @JsonProperty("to") Long to) {
            this.path = path;
            this.from = from;
            this.to = to;
        }

        public String getPath() {
            return path;
        }

        public Long getFrom() {
            return from;
        }

        public Long getTo() {
            return to;
        }
    }
}
