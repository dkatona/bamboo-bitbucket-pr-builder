package cz.katona.pr.builder.bamboo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents branch enabled job in bamboo
 */
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
                  @JsonProperty("enabled") boolean enabled) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Branch branch = (Branch) o;

        if (enabled != branch.enabled) return false;
        if (!name.equals(branch.name)) return false;
        if (!shortName.equals(branch.shortName)) return false;
        return key.equals(branch.key);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + shortName.hashCode();
        result = 31 * result + key.hashCode();
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }
}
