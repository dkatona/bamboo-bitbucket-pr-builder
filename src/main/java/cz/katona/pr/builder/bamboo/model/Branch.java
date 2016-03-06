package cz.katona.pr.builder.bamboo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Branch {

    private String name;
    private String shortName;
    private String key;
    private boolean enabled;

    @JsonCreator
    public Branch(@JsonProperty("name") String name,
                  @JsonProperty("shortName") String shortName,
                  @JsonProperty("key") String key,
                  @JsonProperty("boolean") boolean enabled) {
        this.name = name;
        this.shortName = shortName;
        this.key = key;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getKey() {
        return key;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
