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
    /**
     * Temporary folder used so command-processing tests do not touch real storage.
     */
    @TempDir
    public Path temporaryFolder;

    /**
     * Verifies that task commands update state and return display-ready responses.
     */
    @Test
    public void processCommand_taskCommands_returnResponsesAndUpdateState() {
        Baymax baymax = new Baymax(
                temporaryFolder.resolve("Baymax.txt").toString());

        Baymax.CommandResponse added = baymax.processCommand("todo buy milk");
        assertFalse(added.shouldExit());
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
        assertEquals(" I am satisfied with my care. Until next time.", response.message());
        assertEquals("T | 0 | buy milk" + System.lineSeparator(),
                Files.readString(filePath));
    }
}
