/**
 * Represents a task that must be completed before a specified date or time.
 */
public class Deadline extends Task {

    /** The date or time by which the task should be completed. */
    protected String by;

    /**
     * Creates a new unfinished deadline task.
     *
     * @param description the text describing the task
     * @param by the date or time by which the task should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline type marker, task details, and due date or time.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
