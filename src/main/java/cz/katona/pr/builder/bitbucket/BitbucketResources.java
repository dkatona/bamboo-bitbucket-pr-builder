package cz.katona.pr.builder.bitbucket;

import org.springframework.web.util.UriTemplate;

public class BitbucketResources {

    /**
     * Resource for creating comments, required parameters:
     * <ul>
     *     <li>baseUri</li>
     *     <li>repo_full_name</li>
     *     <li>pull_id</li>
     * </ul>
     */
    public static final UriTemplate COMMENTS_RESOURCE = new UriTemplate("{baseUri}/repositories/{repo_full_name}/pullrequests/{pull_id}/comments");

    /**
     * Resource for getting information about main branch:
     * <ul>
     *     <li>baseUri</li>
     *     <li>repo_full_name</li>
     * </ul>
     */
    public static final UriTemplate MAIN_BRANCH_RESOURCE = new UriTemplate("{baseUri}/repositories/{repo_full_name}/main-branch");

    /**
     * Response from @{MAIN_BRANCH_RESOURCE} when there is not a main branch set
     */
    public static final String NO_BRANCH_SET_RESPONSE = "No main branch set";

    private BitbucketResources() {
    }
}
