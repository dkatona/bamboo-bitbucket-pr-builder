package cz.katona.pr.builder.bitbucket;

import cz.katona.pr.builder.bitbucket.model.CommentAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BitbucketService {

    private final BitbucketClient bitbucketClient;

    @Autowired
    public BitbucketService(BitbucketClient bitbucketClient) {
        this.bitbucketClient = bitbucketClient;
    }

    public void createComment(String repositoryFullName, Long pullId, String commentContent, Long commentParentId) {
        CommentAdd comment = new CommentAdd(commentContent, commentParentId);
        bitbucketClient.post(BitbucketResources.COMMENTS_RESOURCE.expand(repositoryFullName, pullId).toString(),
                comment, null);
    }

}
