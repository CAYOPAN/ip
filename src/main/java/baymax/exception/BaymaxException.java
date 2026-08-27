package baymax.exception;

/**
 * Represents an error that Baymax can show to the user as a friendly message.
 */
public class BaymaxException extends RuntimeException {
    /**
     * Creates a Baymax exception with the given user-facing message.
     *
     * @param message the message to show to the user
     */
    public BaymaxException(String message) {
        super(message);
    }
}
