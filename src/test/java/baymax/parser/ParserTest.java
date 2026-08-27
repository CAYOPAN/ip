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

    @Test
    public void getCommandType_unknownCommand_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                Parser.getCommandType("remind me"));
    }

    @Test
    public void getCommandType_commandMissingRequiredSpace_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                Parser.getCommandType("todo"));
    }

    @Test
    public void parseTaskIndex_markCommand_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("mark 1", Parser.CommandType.MARK));
        assertEquals(11, Parser.parseTaskIndex("mark 12", Parser.CommandType.MARK));
    }

    @Test
    public void parseTaskIndex_unmarkCommand_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("unmark 1", Parser.CommandType.UNMARK));
        assertEquals(11, Parser.parseTaskIndex("unmark 12", Parser.CommandType.UNMARK));
    }

    @Test
    public void parseTaskIndex_deleteCommand_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("delete 1", Parser.CommandType.DELETE));
        assertEquals(11, Parser.parseTaskIndex("delete 12", Parser.CommandType.DELETE));
    }

    @Test
    public void parseTaskIndex_commandWithExtraSpaces_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseTaskIndex("mark   1   ", Parser.CommandType.MARK));
        assertEquals(11, Parser.parseTaskIndex("unmark   12   ", Parser.CommandType.UNMARK));
        assertEquals(4, Parser.parseTaskIndex("delete   5   ", Parser.CommandType.DELETE));
    }

    @Test
    public void parseTaskIndex_missingTaskNumber_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseTaskIndex("delete", Parser.CommandType.DELETE));

        assertEquals(" Sorry, please provide a valid task number.",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonNumericTaskNumber_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseTaskIndex("mark abc", Parser.CommandType.MARK));

        assertEquals(" Sorry, please provide a valid task number.",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_unsupportedCommandType_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                Parser.parseTaskIndex("todo read book", Parser.CommandType.TODO));
    }

    @Test
    public void parseTodoDescription_validCommand_returnsDescription() {
        assertEquals("read book", Parser.parseTodoDescription("todo read book"));
    }

    @Test
    public void parseTodoDescription_extraSpaces_returnsTrimmedDescription() {
        assertEquals("read book", Parser.parseTodoDescription("todo   read book   "));
    }

    @Test
    public void parseTodoDescription_emptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseTodoDescription("todo"));
    }

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

    @Test
    public void parseDeadline_extraSpaces_returnsTrimmedDeadlineDetails() {
        Parser.DeadlineDetails details = Parser.parseDeadline(
                "deadline   submit report   /by   2019-12-02   ");

        assertEquals("submit report", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.date());
    }

    @Test
    public void parseDeadline_missingByMarker_throwsEmptyByException() {
        assertThrows(EmptyByException.class, () ->
                Parser.parseDeadline("deadline submit report"));
    }

    @Test
    public void parseDeadline_emptyByDate_throwsEmptyByException() {
        assertThrows(EmptyByException.class, () ->
                Parser.parseDeadline("deadline submit report /by"));
    }

    @Test
    public void parseDeadline_emptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseDeadline("deadline /by 2019-12-02"));
    }

    @Test
    public void parseDeadline_invalidDateFormat_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseDeadline("deadline submit report /by 02-12-2019"));

        assertEquals(" Sorry, dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_validCommand_returnsEventDetails() {
        Parser.EventDetails details = Parser.parseEvent(
                "event team meeting /from 2019-12-02 /to 2019-12-04");

        assertEquals("team meeting", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.from());
        assertEquals(LocalDate.of(2019, 12, 4), details.to());
    }

    @Test
    public void parseEvent_extraSpaces_returnsTrimmedEventDetails() {
        Parser.EventDetails details = Parser.parseEvent(
                "event   team meeting   /from   2019-12-02   /to   2019-12-04   ");

        assertEquals("team meeting", details.description());
        assertEquals(LocalDate.of(2019, 12, 2), details.from());
        assertEquals(LocalDate.of(2019, 12, 4), details.to());
    }

    @Test
    public void parseEvent_missingFromMarker_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseEvent("event team meeting /to 2019-12-04"));
    }

    @Test
    public void parseEvent_emptyDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () ->
                Parser.parseEvent("event /from 2019-12-02 /to 2019-12-04"));
    }

    @Test
    public void parseEvent_emptyFromDate_throwsEmptyFromException() {
        assertThrows(EmptyFromException.class, () ->
                Parser.parseEvent("event team meeting /from /to 2019-12-04"));
    }

    @Test
    public void parseEvent_missingToMarker_throwsEmptyFromException() {
        assertThrows(EmptyFromException.class, () ->
                Parser.parseEvent("event team meeting /from 2019-12-02"));
    }

    @Test
    public void parseEvent_emptyToDate_throwsEmptyToException() {
        assertThrows(EmptyToException.class, () ->
                Parser.parseEvent("event team meeting /from 2019-12-02 /to"));
    }

    @Test
    public void parseEvent_invalidFromDateFormat_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseEvent("event team meeting /from 02-12-2019 /to 2019-12-04"));

        assertEquals(" Sorry, dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_invalidToDateFormat_throwsBaymaxException() {
        BaymaxException exception = assertThrows(BaymaxException.class, () ->
                Parser.parseEvent("event team meeting /from 2019-12-02 /to 04-12-2019"));

        assertEquals(" Sorry, dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }
}
