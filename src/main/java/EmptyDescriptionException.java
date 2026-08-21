public class EmptyDescriptionException extends BaymaxException {
    public EmptyDescriptionException(String task) {
        super("     OOPS!!! The description of a" + task + "cannot be empty.");
    }
}
