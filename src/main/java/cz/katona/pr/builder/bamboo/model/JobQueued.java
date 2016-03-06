package cz.katona.pr.builder.bamboo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobQueued {

    private String planKey;
    private String buildNumber;
    private String buildResultKey;
    private String triggerReason;

    public JobQueued(
            @JsonProperty("planKey") String planKey,
            @JsonProperty("buildNumber") String buildNumber,
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

    public String getBuildNumber() {
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
}
