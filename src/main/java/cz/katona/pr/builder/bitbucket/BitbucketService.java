package cz.katona.pr.builder.bitbucket;

public interface BitbucketService {
    void createComment(String repositoryFullName, Long pullId, String commentContent, Long commentParentId);
}
