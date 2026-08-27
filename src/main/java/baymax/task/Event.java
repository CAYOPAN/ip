package baymax.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that starts at one specified date and ends at another
 * specified date.
 */
public class Event extends Task {

    /** The date when the event starts. */
    private final LocalDate from;

    /** The date when the event ends. */
    private final LocalDate to;

    /**
     * Creates a new unfinished event task.
     *
     * @param description the text describing the event
     * @param from the date when the event starts
     * @param to the date when the event ends
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    private String getFormattedFrom() {
        return this.from.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
    }

    private String getFormattedTo() {
        return this.to.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
    }

    /**
     * Returns the event type marker, task details, and event duration.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        String from = this.getFormattedFrom();
        String to = this.getFormattedTo();
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toStorageString() {
        return "E" + " | " + super.toStorageString() + " | " + from + " | " + to;
    }
}
