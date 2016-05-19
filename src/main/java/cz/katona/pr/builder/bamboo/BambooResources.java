package cz.katona.pr.builder.bamboo;

import org.springframework.web.util.UriTemplate;

public class BambooResources {

    /**
     * Allows to retrieve information about branch job, required parameters:
     * <ul>
     *     <li>baseUri</li>
     *     <li>planId</li>
     *     <li>branchName</li>
     * </ul>
     */
    public static final UriTemplate BRANCH_RESOURCE = new UriTemplate("{baseUri}/plan/{planId}/branch/{branchName}.json");

    /**
     * Allows to create branch job, required parameters:
     * <ul>
     *     <li>baseUri</li>
     *     <li>planId</li>
     *     <li>branchName</li>
     *     <li>gitBranch</li>
     * </ul>
     */
    public static final UriTemplate BRANCH_CREATE_RESOURCE = new UriTemplate(BRANCH_RESOURCE.toString() + "?vcsBranch={gitBranch}");

    /**
     * Allows enabling branch job, required parameters:
     * <ul>
     *     <li>baseUri</li>
     *     <li>planIdWithBranch</li>
     * </ul>
     */
    public static final UriTemplate BRANCH_ENABLE_RESOURCE = new UriTemplate("{baseUri}/plan/{planIdWithBranch}/enable.json");

    /**
     * Put a new build of the respective branch job to the queue, required parameters:
     * <ul>
     *     <li>baseUri</li>
     *     <li>planIdWithBranch</li>
     * </ul>
     */
    public static final UriTemplate QUEUE_BRANCH_JOB = new UriTemplate("{baseUri}/queue/{planIdWithBranch}.json");

    private BambooResources() {
    }
}
