package cz.katona.pr.builder.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Repository payload as specified <a href="https://confluence.atlassian.com/bitbucket/event-payloads-740262817.html#EventPayloads-entity_repositoryRepository">here</a>.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {

    private String name;

    private String fullName;

    private String uuid;

    private String website;

    private String scm;

    private boolean isPrivate;

    @JsonCreator
    public Repository(@JsonProperty("name") String name,
                      @JsonProperty("full_name") String fullName,
                      @JsonProperty("uuid") String uuid,
                      @JsonProperty("website") String website,
                      @JsonProperty("scm") String scm,
                      @JsonProperty("is_private") boolean isPrivate) {
        this.name = name;
        this.fullName = fullName;
        this.uuid = uuid;
        this.website = website;
        this.scm = scm;
        this.isPrivate = isPrivate;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getWebsite() {
        return website;
    }

    public String getScm() {
        return scm;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}
