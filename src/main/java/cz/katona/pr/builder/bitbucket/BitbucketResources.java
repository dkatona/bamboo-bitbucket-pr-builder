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

    private BitbucketResources() {
    }
}
