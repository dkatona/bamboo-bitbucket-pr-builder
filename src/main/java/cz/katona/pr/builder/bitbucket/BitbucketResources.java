package cz.katona.pr.builder.bitbucket;

import org.springframework.web.util.UriTemplate;

public class BitbucketResources {

    public static final UriTemplate COMMENTS_RESOURCE = new UriTemplate("{baseUri}/repositories/{repo_full_name}/pullrequests/{pull_id}/comments");

    private BitbucketResources() {
    }
}
