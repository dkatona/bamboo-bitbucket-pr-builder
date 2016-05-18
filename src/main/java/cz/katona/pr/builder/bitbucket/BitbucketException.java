package cz.katona.pr.builder.bitbucket;

/**
 * Exception signalling problems with Bitbucket (either communication errors or bad response codes)
 */
public class BitbucketException extends RuntimeException {

    public BitbucketException(String message) {
        super(message);
    }

    public BitbucketException(String message, Throwable cause) {
        super(message, cause);
    }
}
