/**
 * Represents a task that starts at one specified date or time and ends at
 * another specified date or time.
 */
public class Event extends Task {

    /** The date or time when the event starts. */
    protected String from;

    /** The date or time when the event ends. */
    protected String to;

    /**
     * Creates a new unfinished event task.
     *
     * @param description the text describing the event
     * @param from the date or time when the event starts
     * @param to the date or time when the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event type marker, task details, and event duration.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
