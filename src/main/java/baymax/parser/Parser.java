package baymax.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import baymax.exception.BaymaxException;
import baymax.exception.EmptyByException;
import baymax.exception.EmptyDescriptionException;
import baymax.exception.EmptyFromException;
import baymax.exception.EmptyToException;
import baymax.exception.InvalidCommandException;

/**
 * Identifies the type of a user command.
 */
public final class Parser {

    /**
     * Supported command types.
     */
    public enum CommandType {
        BYE,
        LIST,
        MARK,
        UNMARK,
        DELETE,
        FIND,
        TODO,
        DEADLINE,
        EVENT
    }

    private Parser() {
        // Utility class.
    }

    /**
     * Identifies the command type.
     *
     * @param command the user's command
     * @return the command type
     */
    public static CommandType getCommandType(String command) {
        if (command.equals("bye")) {
            return CommandType.BYE;
        } else if (command.equals("list")) {
            return CommandType.LIST;
        } else if (command.startsWith("mark ")) {
            return CommandType.MARK;
        } else if (command.startsWith("unmark ")) {
            return CommandType.UNMARK;
        } else if (command.equals("delete")
                || command.startsWith("delete ")) {
            return CommandType.DELETE;
        } else if (command.startsWith("find ")) {
            return CommandType.FIND;
        } else if (command.startsWith("todo ")) {
            return CommandType.TODO;
        } else if (command.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        } else if (command.startsWith("event ")) {
            return CommandType.EVENT;
        }

        throw new InvalidCommandException();
    }

    /**
     * Parses a task number into a zero-based task index.
     *
     * @param command the complete command
     * @param commandType the command type
     * @return the zero-based task index
     */
    public static int parseTaskIndex(
            String command, CommandType commandType) {

        String taskNumberText;

        if (commandType == CommandType.MARK) {
            assert command.startsWith("mark ")
                    : "Mark index parsing should receive mark command text.";
            taskNumberText = command.substring("mark ".length()).trim();
        } else if (commandType == CommandType.UNMARK) {
            assert command.startsWith("unmark ")
                    : "Unmark index parsing should receive unmark command text.";
            taskNumberText = command.substring("unmark ".length()).trim();
        } else if (commandType == CommandType.DELETE) {
            assert command.equals("delete") || command.startsWith("delete ")
                    : "Delete index parsing should receive delete command text.";
            taskNumberText = command.equals("delete")
                    ? ""
                    : command.substring("delete ".length()).trim();
        } else {
            throw new InvalidCommandException();
        }

        try {
            return Integer.parseInt(taskNumberText) - 1;
        } catch (NumberFormatException exception) {
            throw new BaymaxException(
                    " Sorry, please provide a valid task number.");
        }
    }

    /**
     * Extracts a todo description.
     *
     * @param command the complete todo command
     * @return the todo description
     */
    public static String parseTodoDescription(String command) {
        assert command.startsWith("todo")
                : "Todo description parsing should receive todo command text.";
        String description =
                command.substring("todo".length()).trim();

        if (description.isEmpty()) {
            throw new EmptyDescriptionException("todo");
        }

        return description;
    }

    /**
     * Extracts the keyword used to search task descriptions.
     *
     * @param command the complete find command
     * @return the keyword to search for
     */
    public static String parseFindKeyword(String command) {
        assert command.startsWith("find")
                : "Find keyword parsing should receive find command text.";
        String keyword =
                command.substring("find".length()).trim();

        if (keyword.isEmpty()) {
            throw new EmptyDescriptionException("find");
        }

        return keyword;
    }

    /**
     * Parsed deadline details.
     *
     * @param description the deadline description
     * @param date the deadline date
     */
    public record DeadlineDetails(
            String description, LocalDate date) {
    }

    /**
     * Parsed event details.
     *
     * @param description the event description
     * @param from the event start date
     * @param to the event end date
     */
    public record EventDetails(
            String description,
            LocalDate from,
            LocalDate to) {
    }

    /**
     * Parses a deadline command.
     *
     * @param command the complete deadline command
     * @return parsed deadline details
     */
    public static DeadlineDetails parseDeadline(String command) {
        assert command.startsWith("deadline")
                : "Deadline parsing should receive deadline command text.";
        String deadlineDetails =
                command.substring("deadline".length()).trim();

        int byMarkerIndex = deadlineDetails.indexOf("/by");

        String description = byMarkerIndex < 0
                ? ""
                : deadlineDetails.substring(0, byMarkerIndex).trim();

        String by = byMarkerIndex < 0
                ? ""
                : deadlineDetails.substring(
                byMarkerIndex + "/by".length()).trim();

        if (byMarkerIndex < 0 || by.isEmpty()) {
            throw new EmptyByException();
        }

        if (description.isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }

        return new DeadlineDetails(description, parseDate(by));
    }

    /**
     * Parses an event command.
     *
     * @param command the complete event command
     * @return parsed event details
     */
    public static EventDetails parseEvent(String command) {
        assert command.startsWith("event")
                : "Event parsing should receive event command text.";
        String eventDetails =
                command.substring("event".length()).trim();

        int fromMarkerIndex = eventDetails.indexOf("/from");

        int toMarkerIndex = fromMarkerIndex < 0
                ? -1
                : eventDetails.indexOf(
                "/to",
                fromMarkerIndex + "/from".length());

        String description = fromMarkerIndex < 0
                ? ""
                : eventDetails.substring(0, fromMarkerIndex).trim();

        String from = fromMarkerIndex < 0 || toMarkerIndex < 0
                ? ""
                : eventDetails.substring(
                fromMarkerIndex + "/from".length(),
                toMarkerIndex).trim();

        String to = toMarkerIndex < 0
                ? ""
                : eventDetails.substring(
                toMarkerIndex + "/to".length()).trim();

        if (description.isEmpty()) {
            throw new EmptyDescriptionException("event");
        }

        if (from.isEmpty()) {
            throw new EmptyFromException();
        }

        if (to.isEmpty()) {
            throw new EmptyToException();
        }

        return new EventDetails(
                description,
                parseDate(from),
                parseDate(to));
    }

    /**
     * Parses a date in yyyy-MM-dd format.
     *
     * @param dateText the date text
     * @return the parsed date
     */
    private static LocalDate parseDate(String dateText) {
        assert dateText != null && !dateText.isBlank()
                : "Date parsing should receive non-empty date text.";
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new BaymaxException(
                    " Sorry, dates must use the format yyyy-MM-dd.");
        }
    }
}
