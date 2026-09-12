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
            taskNumberText = command.substring("mark ".length()).trim();
        } else if (commandType == CommandType.UNMARK) {
            taskNumberText = command.substring("unmark ".length()).trim();
        } else if (commandType == CommandType.DELETE) {
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
     * @param dueDate the deadline date
     */
    public record DeadlineDetails(
            String description, LocalDate dueDate) {
    }

    /**
     * Parsed event details.
     *
     * @param description the event description
     * @param startDate the event start date
     * @param endDate the event end date
     */
    public record EventDetails(
            String description,
            LocalDate startDate,
            LocalDate endDate) {
    }

    /**
     * Stores deadline fields before the date text is validated and converted.
     *
     * @param description the deadline description
     * @param dueDateText the unparsed deadline date
     */
    private record DeadlineTextDetails(
            String description, String dueDateText) {
    }

    /**
     * Stores event fields before the date text is validated and converted.
     *
     * @param description the event description
     * @param startDateText the unparsed event start date
     * @param endDateText the unparsed event end date
     */
    private record EventTextDetails(
            String description,
            String startDateText,
            String endDateText) {
    }

    /**
     * Parses a deadline command.
     *
     * @param command the complete deadline command
     * @return parsed deadline details
     */
    public static DeadlineDetails parseDeadline(String command) {
        DeadlineTextDetails details = extractDeadlineTextDetails(command);

        if (details.dueDateText().isEmpty()) {
            throw new EmptyByException();
        }

        if (details.description().isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }

        return new DeadlineDetails(
                details.description(), parseDate(details.dueDateText()));
    }

    /**
     * Parses an event command.
     *
     * @param command the complete event command
     * @return parsed event details
     */
    public static EventDetails parseEvent(String command) {
        EventTextDetails details = extractEventTextDetails(command);

        if (details.description().isEmpty()) {
            throw new EmptyDescriptionException("event");
        }

        if (details.startDateText().isEmpty()) {
            throw new EmptyFromException();
        }

        if (details.endDateText().isEmpty()) {
            throw new EmptyToException();
        }

        return new EventDetails(
                details.description(),
                parseDate(details.startDateText()),
                parseDate(details.endDateText()));
    }

    /**
     * Separates a deadline command into its raw description and date text.
     *
     * @param command the complete deadline command
     * @return the raw deadline fields, with empty values for a missing marker
     */
    private static DeadlineTextDetails extractDeadlineTextDetails(String command) {
        String deadlineDetails =
                command.substring("deadline".length()).trim();
        int byMarkerIndex = deadlineDetails.indexOf("/by");

        if (byMarkerIndex < 0) {
            return new DeadlineTextDetails("", "");
        }

        String description =
                deadlineDetails.substring(0, byMarkerIndex).trim();
        String dueDateText = deadlineDetails.substring(
                byMarkerIndex + "/by".length()).trim();
        return new DeadlineTextDetails(description, dueDateText);
    }

    /**
     * Separates an event command into its raw description and date text.
     *
     * @param command the complete event command
     * @return the raw event fields, with empty values for missing markers
     */
    private static EventTextDetails extractEventTextDetails(String command) {
        String eventDetails =
                command.substring("event".length()).trim();
        int fromMarkerIndex = eventDetails.indexOf("/from");

        if (fromMarkerIndex < 0) {
            return new EventTextDetails("", "", "");
        }

        String description =
                eventDetails.substring(0, fromMarkerIndex).trim();
        int toMarkerIndex = eventDetails.indexOf(
                "/to", fromMarkerIndex + "/from".length());
        if (toMarkerIndex < 0) {
            return new EventTextDetails(description, "", "");
        }

        String startDateText = eventDetails.substring(
                fromMarkerIndex + "/from".length(), toMarkerIndex).trim();
        String endDateText = eventDetails.substring(
                toMarkerIndex + "/to".length()).trim();
        return new EventTextDetails(
                description, startDateText, endDateText);
    }

    /**
     * Parses a date in yyyy-MM-dd format.
     *
     * @param dateText the date text
     * @return the parsed date
     */
    private static LocalDate parseDate(String dateText) {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new BaymaxException(
                    " Sorry, dates must use the format yyyy-MM-dd.");
        }
    }
}
