/**
 * Represents a task in Baymax's task list.
 *
 * <p>A task stores its description together with whether it has been
 * completed. This keeps task-related data and behavior in one place.</p>
 */
public class Task {
    /** The text describing what the task is about. */
    protected String description;

    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /**
     * Creates a new unfinished task.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when done, or a blank space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the text describing the task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the standard text representation of a task.
     *
     * <p>Subclasses can prepend their task-type marker to this representation
     * while reusing the completion status and description formatting.</p>
     *
     * @return the completion marker and task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
