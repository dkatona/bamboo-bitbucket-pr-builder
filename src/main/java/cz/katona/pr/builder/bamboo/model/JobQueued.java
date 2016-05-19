package cz.katona.pr.builder.bamboo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents queued job in bamboo
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobQueued {

    private String planKey;
    private Integer buildNumber;
    private String buildResultKey;
    private String triggerReason;

    @JsonCreator
    public JobQueued(
            @JsonProperty("planKey") String planKey,
            @JsonProperty("buildNumber") Integer buildNumber,
            @JsonProperty("buildResultKey") String buildResultKey,
            @JsonProperty("triggerReason") String triggerReason) {
        this.planKey = planKey;
        this.buildNumber = buildNumber;
        this.buildResultKey = buildResultKey;
        this.triggerReason = triggerReason;
    }

    public String getPlanKey() {
        return planKey;
    }

    public Integer getBuildNumber() {
        return buildNumber;
    }

    public String getBuildResultKey() {
        return buildResultKey;
    }

    public String getTriggerReason() {
        return triggerReason;
    }

    public String getJobBuildLink(String bambooBrowseUri) {
        return bambooBrowseUri + "/" + getBuildResultKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobQueued jobQueued = (JobQueued) o;

        if (!planKey.equals(jobQueued.planKey)) return false;
        if (!buildNumber.equals(jobQueued.buildNumber)) return false;
        if (!buildResultKey.equals(jobQueued.buildResultKey)) return false;
        return triggerReason.equals(jobQueued.triggerReason);

    }

    @Override
    public int hashCode() {
        int result = planKey.hashCode();
        result = 31 * result + buildNumber.hashCode();
        result = 31 * result + buildResultKey.hashCode();
        result = 31 * result + triggerReason.hashCode();
        return result;
    }
}
