package baymax.task;

import java.util.ArrayList;

/**
 * Stores and manages Baymax's tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public Task remove(int index) {
        return tasks.remove(index);
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
        TaskList matchingTasks = new TaskList();
        String lowercaseKeyword = keyword.toLowerCase();

        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowercaseKeyword)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }

    public int size() {
        return tasks.size();
    }
}
