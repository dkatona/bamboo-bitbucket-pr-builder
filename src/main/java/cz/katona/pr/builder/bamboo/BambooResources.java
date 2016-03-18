package cz.katona.pr.builder.bamboo;

import org.springframework.web.util.UriTemplate;

public class BambooResources {

    public static final UriTemplate BRANCH_RESOURCE = new UriTemplate("{baseUri}/plan/{planId}/branch/{branchName}.json");

    public static final UriTemplate BRANCH_CREATE_RESOURCE = new UriTemplate(BRANCH_RESOURCE.toString() + "?vcsBranch={gitBranch}");

    public static final UriTemplate BRANCH_ENABLE_RESOURCE = new UriTemplate("{baseUri}/plan/{planIdWithBranch}/enable.json");

    public static final UriTemplate QUEUE_BRANCH_JOB = new UriTemplate("{baseUri}/queue/{planIdWithBranch}.json");

    private BambooResources() {
    }
}
