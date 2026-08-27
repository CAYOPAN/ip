package baymax.exception;

/**
 * Represents an application-level error that can be shown to the user.
 */
public class BaymaxException extends RuntimeException {

    /**
     * Creates an exception with a user-facing error message.
     */
    public BaymaxException(String message) {
        super(message);
    }
}
