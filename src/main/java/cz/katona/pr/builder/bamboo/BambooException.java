package cz.katona.pr.builder.bamboo;

public class BambooException extends RuntimeException {

    public BambooException(String message) {
        super(message);
    }

    public BambooException(String message, Throwable cause) {
        super(message, cause);
    }
}
