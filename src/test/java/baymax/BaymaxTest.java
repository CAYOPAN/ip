package baymax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the command-processing boundary shared by Baymax's user interfaces.
 */
public class BaymaxTest {
    /** Temporary folder so command tests never touch real storage. */
    @TempDir
    public Path temporaryFolder;

    @Test
    public void processCommand_nonBreakingSpaces_matchesOrdinaryCommands() {
        Baymax normal = new Baymax(temporaryFolder.resolve("normal.txt").toString());
        Baymax pasted = new Baymax(temporaryFolder.resolve("pasted.txt").toString());
        for (String command : new String[]{"todo buy milk", "deadline report /by 2026-09-20",
            "event meeting /from 2026-09-20 /to 2026-09-21", "find milk", "mark 1",
            "unmark 1", "delete 1", "list", "bye"
        }) {
            String pastedCommand = "\u00a0" + command.replace(" ", "\u00a0") + "\u00a0";
            assertEquals(normal.processCommand(command), pasted.processCommand(pastedCommand), command);
        }
    }

    @Test
    public void processCommand_nonBreakingSpacesOnly_returnsFriendlyError() {
        Baymax baymax = new Baymax(temporaryFolder.resolve("Baymax.txt").toString());
        Baymax.CommandResponse response = baymax.processCommand("\u00a0 \t\u00a0");
        assertTrue(response.isError());
        assertFalse(response.shouldExit());
        assertEquals(" I have some concerns." + System.lineSeparator() + " Sorry, please enter a command.",
                response.message());
    }

    @Test
    public void processCommand_whitespaceAndDuplicates_preservesValidState() {
        Baymax baymax = new Baymax(temporaryFolder.resolve("Baymax.txt").toString());
        assertFalse(baymax.processCommand("  todo\t buy   milk  ").isError());
        assertFalse(baymax.processCommand("\tmark\t1  ").isError());
        assertTrue(baymax.processCommand("todo buy milk").isError());
        assertEquals(" Here is your current care plan:" + System.lineSeparator() + " 1.[T][X] buy milk",
                baymax.processCommand(" list  ").message());
    }

    @Test
    public void processCommand_invalidInputs_returnsErrorsAndContinues() {
        Baymax baymax = new Baymax(temporaryFolder.resolve("Baymax.txt").toString());
        for (String command : new String[]{null, "", "  ", "todo", "find", "mark", "unmark", "delete",
            "list extra", "bye extra", "todo unsafe|record", "todo embedded\nline",
            "deadline bad /by 2024-02-30", "event bad /from 2024-01-02 /to 2024-01-01"
        }) {
            Baymax.CommandResponse response = baymax.processCommand(command);
            assertTrue(response.isError(), command);
            assertFalse(response.shouldExit(), command);
        }
        assertEquals(" Here is your current care plan:", baymax.processCommand("list").message());
        assertFalse(baymax.processCommand("todo valid").isError());
    }

    /**
     * Verifies that task commands update state and return display-ready responses.
     */
    @Test
    public void processCommand_taskCommands_returnResponsesAndUpdateState() {
        Baymax baymax = new Baymax(
                temporaryFolder.resolve("Baymax.txt").toString());

        Baymax.CommandResponse added = baymax.processCommand("todo buy milk");
        assertFalse(added.shouldExit());
        assertFalse(added.isError());
        assertEquals(
                " I have added this task to your care plan:" + System.lineSeparator()
                        + "   [T][ ] buy milk" + System.lineSeparator()
                        + " You now have 1 task under my care.",
                added.message());

        Baymax.CommandResponse marked = baymax.processCommand("mark 1");
        assertEquals(
                " Excellent. This task is complete:" + System.lineSeparator()
                        + "   [T][X] buy milk",
                marked.message());

        Baymax.CommandResponse listed = baymax.processCommand("list");
        assertEquals(
                " Here is your current care plan:" + System.lineSeparator()
                        + " 1.[T][X] buy milk",
                listed.message());
    }

    /**
     * Verifies that malformed commands return errors without requesting termination.
     */
    @Test
    public void processCommand_invalidCommand_returnsErrorResponse() {
        Baymax baymax = new Baymax(
                temporaryFolder.resolve("Baymax.txt").toString());

        Baymax.CommandResponse response = baymax.processCommand("deadline report");

        assertFalse(response.shouldExit());
        assertTrue(response.isError());
        assertEquals(
                " I have some concerns." + System.lineSeparator()
                        + " Sorry, a deadline needs a due date.",
                response.message());
    }

    /**
     * Verifies that bye produces an exit response and tasks can be saved afterward.
     *
     * @throws IOException if the temporary task file cannot be read
     */
    @Test
    public void processCommand_bye_returnsExitResponseAndSavesTasks() throws IOException {
        Path filePath = temporaryFolder.resolve("Baymax.txt");
        Baymax baymax = new Baymax(filePath.toString());
        baymax.processCommand("todo buy milk");

        Baymax.CommandResponse response = baymax.processCommand("bye");
        baymax.saveTasks();

        assertTrue(response.shouldExit());
        assertFalse(response.isError());
        assertEquals(" I am satisfied with my care. Until next time.", response.message());
        assertEquals("T | 0 | buy milk" + System.lineSeparator(),
                Files.readString(filePath));
    }
}
