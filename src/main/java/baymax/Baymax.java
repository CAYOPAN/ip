package baymax;

import baymax.exception.BaymaxException;
import baymax.exception.InvalidCommandException;
import baymax.parser.Parser;
import baymax.storage.Storage;
import baymax.task.Deadline;
import baymax.task.Event;
import baymax.task.Task;
import baymax.task.TaskList;
import baymax.task.Todo;
import baymax.ui.Ui;

import java.io.IOException;

/**
 * Runs Baymax's text user interface.
 *
 * <p>
 * Each line entered by the user is stored as a task, unless it is one of
 * the special commands {@code list}, {@code todo}, {@code deadline},
 * {@code event}, {@code mark}, {@code unmark}, or
 * {@code bye}. Tasks are kept only while the program is running, as required
 * for this level.
 * </p>
 */
public class Baymax {
    /**
     * Starts Baymax, reads user commands, updates tasks, and saves them before exit.
     *
     * @param args command-line arguments, currently unused
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("./data/Baymax.txt");
        TaskList tasks = storage.load();

        ui.showWelcome();

        while (ui.hasNextCommand()) {
            try {
                String command = ui.readCommand();
                Parser.CommandType commandType =
                        Parser.getCommandType(command);

                ui.showSeparator();
                if (commandType == Parser.CommandType.BYE) {
                    ui.showGoodbye();
                    try {
                        storage.save(tasks);
                    } catch (IOException io) {
                        ui.showSaveError();
                    }
                    ui.close();
                    break;
                }

                if (commandType == Parser.CommandType.LIST) {
                    ui.showTaskList(tasks);
                } else if (commandType == Parser.CommandType.MARK) {
                    int taskIndex =
                            Parser.parseTaskIndex(command, commandType);
                    if (taskIndex >= 0 && taskIndex < tasks.size()) {
                        tasks.get(taskIndex).markAsDone();
                        ui.showTaskMarked(tasks.get(taskIndex));
                    } else {
                        ui.showTaskNotFound();
                    }
                } else if (commandType == Parser.CommandType.UNMARK) {
                    int taskIndex =
                            Parser.parseTaskIndex(command, commandType);
                    if (taskIndex >= 0 && taskIndex < tasks.size()) {
                        tasks.get(taskIndex).markAsUndone();
                        ui.showTaskUnmarked(tasks.get(taskIndex));
                    } else {
                        ui.showTaskNotFound();
                    }
                } else if (commandType == Parser.CommandType.DELETE) {
                    int taskIndex =
                            Parser.parseTaskIndex(command, commandType);
                    if (taskIndex >= 0 && taskIndex < tasks.size()) {
                        Task removedTask = tasks.remove(taskIndex);
                        ui.showTaskDeleted(removedTask, tasks.size());
                    } else {
                        ui.showTaskNotFound();
                    }
                } else if (commandType == Parser.CommandType.TODO) {
                    String description =
                            Parser.parseTodoDescription(command);
                    tasks.add(new Todo(description));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                } else if (commandType == Parser.CommandType.DEADLINE) {
                    Parser.DeadlineDetails deadline =
                            Parser.parseDeadline(command);

                    tasks.add(new Deadline(
                            deadline.description(),
                            deadline.date()));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                } else if (commandType == Parser.CommandType.EVENT) {
                    Parser.EventDetails event =
                            Parser.parseEvent(command);

                    tasks.add(new Event(
                            event.description(),
                            event.from(),
                            event.to()));
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                } else {
                    throw new InvalidCommandException();
                }
            } catch (BaymaxException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showSeparator();
            }

        }
    }
}
