package cz.katona.pr.builder.bitbucket;

/**
 * Service that allows to perform various operations on bitbucket.
 */
public interface BitbucketService {

    /**
     * Creates a comment in specified pull request
     *
     * @param repositoryFullName name of the bitbucket repository in the form e.g. "bbox/data-platform", can't be empty
     * @param pullId id of the pull request, cannot be null
     * @param commentContent content of the comment
     * @param commentParentId parent of the comment, can be null
     */
    void createComment(String repositoryFullName, Long pullId, String commentContent, Long commentParentId);
}
