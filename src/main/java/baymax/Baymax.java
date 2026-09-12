package baymax;

import java.io.IOException;

import baymax.exception.BaymaxException;
import baymax.parser.Parser;
import baymax.storage.Storage;
import baymax.task.Deadline;
import baymax.task.Event;
import baymax.task.Task;
import baymax.task.TaskList;
import baymax.task.Todo;
import baymax.ui.Ui;

/**
 * Coordinates Baymax's command processing and task state.
 *
 * <p>The command-processing method is deliberately independent of a particular
 * user interface. The console UI and the JavaFX UI can therefore share exactly
 * the same parsing and task-management behavior.</p>
 */
public class Baymax {
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Describes the text response to a command and whether the interface should exit.
     *
     * @param message the response text to display
     * @param shouldExit whether the command requested interface termination
     */
    public record CommandResponse(String message, boolean shouldExit) {
    }

    /**
     * Creates Baymax using the application's default data file.
     */
    public Baymax() {
        this("./data/Baymax.txt");
    }

    /**
     * Creates Baymax using a specified data file.
     *
     * @param filePath the file used to load and save tasks
     */
    public Baymax(String filePath) {
        storage = new Storage(filePath);
        tasks = storage.load();
        assert tasks != null : "Storage.load should always return a TaskList.";
    }

    /**
     * Processes one command and returns the response for an interface to display.
     *
     * @param command the user's command
     * @return the command response and exit status
     */
    public CommandResponse processCommand(String command) {
        try {
            Parser.CommandType commandType = Parser.getCommandType(command);

            return switch (commandType) {
                case BYE -> new CommandResponse(
                        " Bye. Hope to see you again soon!", true);
                case LIST -> new CommandResponse(formatTaskList(
                        " Here are the tasks in your list:", tasks), false);
                case MARK -> processMark(command, commandType);
                case UNMARK -> processUnmark(command, commandType);
                case DELETE -> processDelete(command, commandType);
                case FIND -> processFind(command);
                case TODO -> processTodo(command);
                case DEADLINE -> processDeadline(command);
                case EVENT -> processEvent(command);
            };
        } catch (BaymaxException exception) {
            return new CommandResponse(exception.getMessage(), false);
        }
    }

    /**
     * Saves the current task list.
     *
     * @throws IOException if the task list cannot be written
     */
    public void saveTasks() throws IOException {
        storage.save(tasks);
    }

    private CommandResponse processMark(
            String command, Parser.CommandType commandType) {
        assert commandType == Parser.CommandType.MARK
                : "processMark should only be called for mark commands.";
        int taskIndex = Parser.parseTaskIndex(command, commandType);
        if (isValidTaskIndex(taskIndex)) {
            tasks.get(taskIndex).markAsDone();
            return new CommandResponse(formatTaskChange(
                    " Nice! I've marked this task as done:", tasks.get(taskIndex)), false);
        }
        return new CommandResponse(" Sorry, that task does not exist.", false);
    }

    private CommandResponse processUnmark(
            String command, Parser.CommandType commandType) {
        assert commandType == Parser.CommandType.UNMARK
                : "processUnmark should only be called for unmark commands.";
        int taskIndex = Parser.parseTaskIndex(command, commandType);
        if (isValidTaskIndex(taskIndex)) {
            tasks.get(taskIndex).markAsUndone();
            return new CommandResponse(formatTaskChange(
                    " OK, I've marked this task as not done yet:", tasks.get(taskIndex)), false);
        }
        return new CommandResponse(" Sorry, that task does not exist.", false);
    }

    private CommandResponse processDelete(
            String command, Parser.CommandType commandType) {
        assert commandType == Parser.CommandType.DELETE
                : "processDelete should only be called for delete commands.";
        int taskIndex = Parser.parseTaskIndex(command, commandType);
        if (isValidTaskIndex(taskIndex)) {
            Task removedTask = tasks.remove(taskIndex);
            return new CommandResponse(formatDeletedTask(removedTask), false);
        }
        return new CommandResponse(" Sorry, that task does not exist.", false);
    }

    private CommandResponse processFind(String command) {
        String keyword = Parser.parseFindKeyword(command);
        assert keyword != null && !keyword.isBlank()
                : "Parser should return a non-blank find keyword.";
        return new CommandResponse(formatTaskList(
                " Here are the matching tasks in your list:", tasks.find(keyword)), false);
    }

    private CommandResponse processTodo(String command) {
        String description = Parser.parseTodoDescription(command);
        Task task = new Todo(description);
        tasks.add(task);
        return new CommandResponse(formatAddedTask(task), false);
    }

    private CommandResponse processDeadline(String command) {
        Parser.DeadlineDetails deadline = Parser.parseDeadline(command);
        Task task = new Deadline(deadline.description(), deadline.dueDate());
        tasks.add(task);
        return new CommandResponse(formatAddedTask(task), false);
    }

    private CommandResponse processEvent(String command) {
        Parser.EventDetails event = Parser.parseEvent(command);
        Task task = new Event(
                event.description(), event.startDate(), event.endDate());
        tasks.add(task);
        return new CommandResponse(formatAddedTask(task), false);
    }

    private boolean isValidTaskIndex(int taskIndex) {
        return taskIndex >= 0 && taskIndex < tasks.size();
    }

    private String formatTaskList(String heading, TaskList taskList) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < taskList.size(); i++) {
            response.append(System.lineSeparator())
                    .append(" ")
                    .append(i + 1)
                    .append(".")
                    .append(taskList.get(i));
        }
        return response.toString();
    }

    private String formatTaskChange(String heading, Task task) {
        return heading + System.lineSeparator() + "   " + task;
    }

    private String formatAddedTask(Task task) {
        return " Got it. I've added this task:" + System.lineSeparator()
                + "   " + task + System.lineSeparator()
                + " Now you have " + tasks.size() + " tasks in the list.";
    }

    private String formatDeletedTask(Task task) {
        return " Noted. I've removed this task:" + System.lineSeparator()
                + "   " + task + System.lineSeparator()
                + " Now you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Starts Baymax's text user interface.
     *
     * @param args command-line arguments, currently unused
     */
    public static void main(String[] args) {
        Baymax baymax = new Baymax();
        Ui ui = new Ui();

        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();

            CommandResponse response = baymax.processCommand(command);
            ui.showResponse(response.message());

            if (response.shouldExit()) {
                try {
                    baymax.saveTasks();
                } catch (IOException exception) {
                    ui.showSaveError();
                }
                ui.close();
                ui.showSeparator();
                break;
            }

            ui.showSeparator();
        }
    }
}
