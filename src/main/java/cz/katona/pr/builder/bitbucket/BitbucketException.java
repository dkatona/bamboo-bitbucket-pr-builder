package cz.katona.pr.builder.bitbucket;

public class BitbucketException extends RuntimeException {

    public BitbucketException(String message) {
        super(message);
    }

    public BitbucketException(String message, Throwable cause) {
        super(message, cause);
    }
}
