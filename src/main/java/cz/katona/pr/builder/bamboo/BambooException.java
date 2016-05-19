package cz.katona.pr.builder.bamboo;

/**
 * Exception signalling problems with Bamboo (either communication errors or bad response codes)
 */
public class BambooException extends RuntimeException {

    public BambooException(String message) {
        super(message);
    }

    public BambooException(String message, Throwable cause) {
        super(message, cause);
    }
}
