package cz.katona.pr.builder.bamboo;

import org.springframework.web.util.UriTemplate;

class BambooResources {

    public static final UriTemplate BRANCH_RESOURCE = new UriTemplate("/plan/{planId}/branch/{branchName}");

    public static final UriTemplate BRANCH_CREATE_RESOURCE = new UriTemplate(BRANCH_RESOURCE.toString() + "?vcsBranch={gitBranch}");

    public static final UriTemplate QUEUE_BRANCH_JOB = new UriTemplate("/queue/{planIdWithBranch}");

    private BambooResources() {
    }
}
