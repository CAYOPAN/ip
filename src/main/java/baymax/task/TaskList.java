package baymax.task;

import java.util.ArrayList;

/**
 * Stores and manages Baymax's tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the provided task collection.
     *
     * @param tasks the tasks to manage
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null && !tasks.contains(null)
                : "TaskList should be backed by a collection of non-null tasks.";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "TaskList should only contain real tasks.";
        tasks.add(task);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at the index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "Task retrieval should use a valid zero-based index.";
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size()
                : "Task removal should use a valid zero-based index.";
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * <p>The search is case-insensitive so that users can find tasks without
     * remembering the exact capitalization used when the task was added.</p>
     *
     * @param keyword the keyword to search for
     * @return a task list containing matching tasks in their original order
     */
    public TaskList find(String keyword) {
        assert keyword != null && !keyword.isBlank()
                : "Task search should receive a validated keyword.";

        TaskList matchingTasks = new TaskList();
        String lowercaseKeyword = keyword.toLowerCase();

        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowercaseKeyword)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }
}
