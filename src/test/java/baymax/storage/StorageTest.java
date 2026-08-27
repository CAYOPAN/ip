package baymax.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import baymax.task.Deadline;
import baymax.task.Event;
import baymax.task.TaskList;
import baymax.task.Todo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests task persistence without using the application's real data file.
 */
public class StorageTest {

    /**
     * Temporary folder used so persistence tests do not touch the real data file.
     */
    @TempDir
    public Path temporaryFolder;

    /**
     * Verifies that saving tasks creates missing folders and writes storage records.
     */
    @Test
    public void save_tasksWithNestedFilePath_createsParentDirectoryAndWritesTasks()
            throws IOException {
        Path filePath = temporaryFolder.resolve("data").resolve("Baymax.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline(
                "submit report", LocalDate.of(2019, 12, 2));
        Event event = new Event(
                "team meeting",
                LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4));
        deadline.markAsDone();

        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);
        storage.save(tasks);

        assertTrue(Files.exists(filePath));
        assertEquals(String.join(System.lineSeparator(),
                        "T | 0 | read book",
                        "D | 1 | submit report | 2019-12-02",
                        "E | 0 | team meeting | 2019-12-02 | 2019-12-04")
                        + System.lineSeparator(),
                Files.readString(filePath));
    }

    /**
     * Verifies that loading from a missing data file returns an empty task list.
     */
    @Test
    public void load_missingFile_returnsEmptyTaskList() {
        Path filePath = temporaryFolder.resolve("missing").resolve("Baymax.txt");
        Storage storage = new Storage(filePath.toString());

        TaskList tasks = storage.load();

        assertEquals(0, tasks.size());
    }

    /**
     * Verifies that valid saved records are restored as the correct task types.
     */
    @Test
    public void load_validTaskRecords_returnsRestoredTasks() throws IOException {
        Path filePath = temporaryFolder.resolve("Baymax.txt");
        Files.writeString(filePath, String.join(System.lineSeparator(),
                "T | 0 | read book",
                "D | 1 | submit report | 2019-12-02",
                "E | 0 | team meeting | 2019-12-02 | 2019-12-04"));
        Storage storage = new Storage(filePath.toString());

        TaskList tasks = storage.load();

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[D][X] submit report (by: Dec 02 2019)",
                tasks.get(1).toString());
        assertEquals("[E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)",
                tasks.get(2).toString());
    }

    /**
     * Verifies that malformed records are skipped while valid records still load.
     */
    @Test
    public void load_malformedTaskRecords_skipsInvalidRecords() throws IOException {
        Path filePath = temporaryFolder.resolve("Baymax.txt");
        Files.writeString(filePath, String.join(System.lineSeparator(),
                "T | 0 | valid todo",
                "T | 0",
                "T | maybe | invalid done flag",
                "X | 0 | unknown task type",
                "D | 0 | invalid deadline | 2019-99-99",
                "D | 0 | deadline with extra field | 2019-12-02 | extra",
                "E | 0 | invalid event | 2019-12-02 | not-a-date",
                "E | 0 | event with missing to | 2019-12-02"));
        Storage storage = new Storage(filePath.toString());

        TaskList tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] valid todo", tasks.get(0).toString());
    }
}
