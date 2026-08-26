import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

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
    private static LocalDate parseDate(String dateText) {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new BaymaxException(" Sorry, dates must use the format yyyy-MM-dd.");
        }
    }

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("./data/Baymax.txt");
        TaskList tasks = storage.load();

        ui.showWelcome();

        while (ui.hasNextCommand()) {
            try {
                String command = ui.readCommand();
                Parser.CommandType commandType = Parser.getCommandType(command);

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
                    String deadlineDetails = command.substring("deadline".length()).trim();
                    int byMarkerIndex = deadlineDetails.indexOf("/by");
                    String description = byMarkerIndex < 0
                            ? ""
                            : deadlineDetails.substring(0, byMarkerIndex).trim();
                    String by = byMarkerIndex < 0
                            ? ""
                            : deadlineDetails.substring(byMarkerIndex + "/by".length()).trim();

                    if (byMarkerIndex < 0 || by.isEmpty()) {
                        throw new EmptyByException();
                    } else if (description.isEmpty()) {
                        throw new EmptyDescriptionException("deadline");
                    } else {
                        tasks.add(new Deadline(description, parseDate(by)));
                        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    }
                } else if (commandType == Parser.CommandType.EVENT) {
                    String eventDetails = command.substring("event".length()).trim();
                    int fromMarkerIndex = eventDetails.indexOf("/from");
                    int toMarkerIndex = fromMarkerIndex < 0
                            ? -1
                            : eventDetails.indexOf("/to", fromMarkerIndex + "/from".length());
                    String description = fromMarkerIndex < 0
                            ? ""
                            : eventDetails.substring(0, fromMarkerIndex).trim();
                    String from = fromMarkerIndex < 0 || toMarkerIndex < 0
                            ? ""
                            : eventDetails.substring(fromMarkerIndex + "/from".length(), toMarkerIndex).trim();
                    String to = toMarkerIndex < 0
                            ? ""
                            : eventDetails.substring(toMarkerIndex + "/to".length()).trim();

                    if (description.isEmpty()) {
                        throw new EmptyDescriptionException("event");
                    } else if (from.isEmpty()) {
                        throw new EmptyFromException();
                    } else if (to.isEmpty()) {
                        throw new EmptyToException();
                    } else {
                        tasks.add(new Event(description, parseDate(from), parseDate(to)));
                        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                    }
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
