package baymax.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import baymax.exception.BaymaxException;
import baymax.exception.EmptyByException;
import baymax.exception.EmptyDescriptionException;
import baymax.exception.EmptyFromException;
import baymax.exception.EmptyToException;
import baymax.exception.InvalidCommandException;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests command parsing behavior that can be checked without running the
 * interactive application.
 */
public class ParserTest {

    /**
     * Verifies that each supported command text maps to the correct command type.
     */
    @Test
    public void getCommandType_supportedCommands_returnsMatchingCommandTypes() {
        assertEquals(Parser.CommandType.BYE, Parser.getCommandType("bye"));
        assertEquals(Parser.CommandType.LIST, Parser.getCommandType("list"));
        assertEquals(Parser.CommandType.MARK, Parser.getCommandType("mark 1"));
        assertEquals(Parser.CommandType.UNMARK, Parser.getCommandType("unmark 1"));
        assertEquals(Parser.CommandType.DELETE, Parser.getCommandType("delete"));
        assertEquals(Parser.CommandType.DELETE, Parser.getCommandType("delete 1"));
        assertEquals(Parser.CommandType.FIND, Parser.getCommandType("find book"));
        assertEquals(Parser.CommandType.TODO, Parser.getCommandType("todo read book"));
        assertEquals(Parser.CommandType.DEADLINE, Parser.getCommandType(
                "deadline submit report /by 2019-12-02"));
        assertEquals(Parser.CommandType.EVENT, Parser.getCommandType(
                "event team meeting /from 2019-12-02 /to 2019-12-04"));
    }

    /**
     * Verifies that unknown command text is rejected.
     */
    @Test
    public void getCommandType_unknownCommand_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                Parser.getCommandType("remind me"));
    }

    /**
     * Verifies that commands requiring an argument are rejected when the space is missing.
     */
    @Test
    public void getCommandType_commandMissingRequiredSpace_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                Parser.getCommandType("todo"));
    }

    /**
     * Verifies that mark commands convert one-based task numbers to zero-based indexes.
     */
    @Test
    public void parseTaskIndex_markCommand_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("mark 1", Parser.CommandType.MARK));
        assertEquals(11, Parser.parseTaskIndex("mark 12", Parser.CommandType.MARK));
    }

    /**
     * Verifies that unmark commands convert one-based task numbers to zero-based indexes.
     */
    @Test
    public void parseTaskIndex_unmarkCommand_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("unmark 1", Parser.CommandType.UNMARK));
        assertEquals(11, Parser.parseTaskIndex("unmark 12", Parser.CommandType.UNMARK));
    }

    /**
     * Verifies that delete commands convert one-based task numbers to zero-based indexes.
     */
    @Test
    public void parseTaskIndex_deleteCommand_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("delete 1", Parser.CommandType.DELETE));
        assertEquals(11, Parser.parseTaskIndex("delete 12", Parser.CommandType.DELETE));
    }

    /**
     * Verifies that extra whitespace around task numbers is ignored.
     */
    @Test
    public void parseTaskIndex_commandWithExtraSpaces_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("mark   1   ", Parser.CommandType.MARK));
        assertEquals(11, Parser.parseTaskIndex("unmark   12   ", Parser.CommandType.UNMARK));
        assertEquals(4, Parser.parseTaskIndex("delete   5   ", Parser.CommandType.DELETE));
    }

    /**
     * Verifies that a missing task number produces a helpful parser error.
     */
    @Test
    public void parseTaskIndex_missingTaskNumber_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseTaskIndex("delete", Parser.CommandType.DELETE));

        assertEquals(" Sorry, please provide a valid task number.",
                exception.getMessage());
    }

    /**
     * Verifies that non-numeric task numbers are rejected.
     */
    @Test
    public void parseTaskIndex_nonNumericTaskNumber_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseTaskIndex("mark abc", Parser.CommandType.MARK));

        assertEquals(" Sorry, please provide a valid task number.",
                exception.getMessage());
    }

    /**
     * Verifies that task-number parsing rejects command types without task indexes.
     */
    @Test
    public void parseTaskIndex_unsupportedCommandType_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                Parser.parseTaskIndex("todo read book", Parser.CommandType.TODO));
    }

    /**
     * Verifies that a valid todo command returns its description.
     */
    @Test
    public void parseTodoDescription_validCommand_returnsDescription() {
        assertEquals("read book", Parser.parseTodoDescription("todo read book"));
    }

    /**
     * Verifies that todo descriptions are trimmed.
     */
    @Test
    public void parseTodoDescription_extraSpaces_returnsTrimmedDescription() {
        assertEquals("read book", Parser.parseTodoDescription("todo   read book   "));
    }

    /**
     * Verifies that empty todo descriptions are rejected.
     */
    @Test
    public void parseTodoDescription_emptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseTodoDescription("todo"));
    }

    /**
     * Verifies that a valid deadline command returns its description and date.
     */
    @Test
    public void parseFindKeyword_validCommand_returnsKeyword() {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    @Test
    public void parseFindKeyword_extraSpaces_returnsTrimmedKeyword() {
        assertEquals("book", Parser.parseFindKeyword("find   book   "));
    }

    @Test
    public void parseFindKeyword_emptyKeyword_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseDeadline_validCommand_returnsDeadlineDetails() {
        Parser.DeadlineDetails details = Parser.parseDeadline(
                "deadline submit report /by 2019-12-02");

        assertEquals("submit report", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.date());
    }

    /**
     * Verifies that deadline descriptions and dates are trimmed.
     */
    @Test
    public void parseDeadline_extraSpaces_returnsTrimmedDeadlineDetails() {
        Parser.DeadlineDetails details = Parser.parseDeadline(
                "deadline   submit report   /by   2019-12-02   ");

        assertEquals("submit report", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.date());
    }

    /**
     * Verifies that deadline commands require the by marker.
     */
    @Test
    public void parseDeadline_missingByMarker_throwsEmptyByException() {
        assertThrows(EmptyByException.class, () ->
                Parser.parseDeadline("deadline submit report"));
    }

    /**
     * Verifies that deadline commands require a date after the by marker.
     */
    @Test
    public void parseDeadline_emptyByDate_throwsEmptyByException() {
        assertThrows(EmptyByException.class, () ->
                Parser.parseDeadline("deadline submit report /by"));
    }

    /**
     * Verifies that deadline commands require a description before the by marker.
     */
    @Test
    public void parseDeadline_emptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseDeadline("deadline /by 2019-12-02"));
    }

    /**
     * Verifies that deadline dates must use the supported date format.
     */
    @Test
    public void parseDeadline_invalidDateFormat_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseDeadline("deadline submit report /by 02-12-2019"));

        assertEquals(" Sorry, dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    /**
     * Verifies that a valid event command returns its description and dates.
     */
    @Test
    public void parseEvent_validCommand_returnsEventDetails() {
        Parser.EventDetails details = Parser.parseEvent(
                "event team meeting /from 2019-12-02 /to 2019-12-04");

        assertEquals("team meeting", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.from());
        assertEquals(LocalDate.of(2019, 12, 4), details.to());
    }

    /**
     * Verifies that event descriptions and dates are trimmed.
     */
    @Test
    public void parseEvent_extraSpaces_returnsTrimmedEventDetails() {
        Parser.EventDetails details = Parser.parseEvent(
                "event   team meeting   /from   2019-12-02   /to   2019-12-04   ");

        assertEquals("team meeting", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.from());
        assertEquals(LocalDate.of(2019, 12, 4), details.to());
    }

    /**
     * Verifies the current error shown when an event command omits the from marker.
     */
    @Test
    public void parseEvent_missingFromMarker_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseEvent("event team meeting /to 2019-12-04"));
    }

    /**
     * Verifies that event commands require a description before the from marker.
     */
    @Test
    public void parseEvent_emptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseEvent("event /from 2019-12-02 /to 2019-12-04"));
    }

    /**
     * Verifies that event commands require a start date.
     */
    @Test
    public void parseEvent_emptyFromDate_throwsEmptyFromException() {
        assertThrows(EmptyFromException.class, () ->
                Parser.parseEvent("event team meeting /from /to 2019-12-04"));
    }

    /**
     * Verifies the current error shown when an event command omits the to marker.
     */
    @Test
    public void parseEvent_missingToMarker_throwsEmptyFromException() {
        assertThrows(EmptyFromException.class, () ->
                Parser.parseEvent("event team meeting /from 2019-12-02"));
    }

    /**
     * Verifies that event commands require an end date.
     */
    @Test
    public void parseEvent_emptyToDate_throwsEmptyToException() {
        assertThrows(EmptyToException.class, () ->
                Parser.parseEvent("event team meeting /from 2019-12-02 /to"));
    }

    /**
     * Verifies that event start dates must use the supported date format.
     */
    @Test
    public void parseEvent_invalidFromDateFormat_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseEvent("event team meeting /from 02-12-2019 /to 2019-12-04"));

        assertEquals(" Sorry, dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    /**
     * Verifies that event end dates must use the supported date format.
     */
    @Test
    public void parseEvent_invalidToDateFormat_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseEvent("event team meeting /from 2019-12-02 /to 04-12-2019"));

        assertEquals(" Sorry, dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }
}
