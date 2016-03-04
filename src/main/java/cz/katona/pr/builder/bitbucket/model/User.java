package cz.katona.pr.builder.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload for user as specified <a href="https://confluence.atlassian.com/bitbucket/event-payloads-740262817.html#EventPayloads-entity_userUser">here</a>.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String username;

    private String displayName;

    private String uuid;

    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("display_name") String displayName,
                @JsonProperty("uuid") String uuid) {
        this.username = username;
        this.displayName = displayName;
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUuid() {
        return uuid;
    }
}
