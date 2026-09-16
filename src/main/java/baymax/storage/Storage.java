package baymax.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import baymax.exception.BaymaxException;
import baymax.task.Deadline;
import baymax.task.Event;
import baymax.task.Task;
import baymax.task.TaskList;
import baymax.task.Todo;

/**
 * Loads tasks from and saves tasks to Baymax's data file.
 */
public class Storage {
    private static final int TASK_TYPE_FIELD_INDEX = 0;
    private static final int COMPLETION_STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int PRIMARY_DATE_FIELD_INDEX = 3;
    private static final int EVENT_END_DATE_FIELD_INDEX = 4;

    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private static final String STORAGE_FIELD_SEPARATOR_REGEX = "\\s*\\|\\s*";
    private static final String COMPLETED_STATUS = "1";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";

    private final String filePath;
    private String loadWarning = "";

    /**
     * Creates a storage helper that reads from and writes to the given file path.
     *
     * @param filePath the data file path used to persist tasks
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank()
                : "Storage should be configured with a real file path.";
        this.filePath = filePath;
    }

    /**
     * Saves all tasks to the configured data file, creating parent folders if needed.
     *
     * @param taskList the tasks to save
     * @throws IOException if the file cannot be written
     */
    public void save(TaskList taskList) throws IOException {
        assert taskList != null : "Storage.save should receive a task list.";

        if (!loadWarning.isEmpty()) {
            throw new IOException("Saving is disabled to protect the original data file. "
                    + "Repair the file and restart Baymax.");
        }
        Path target = Path.of(filePath).toAbsolutePath();
        Files.createDirectories(target.getParent());
        Path temporary = Files.createTempFile(target.getParent(), "baymax-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporary)) {
                for (int i = 0; i < taskList.size(); i++) {
                    writer.write(taskList.get(i).toStorageString());
                    writer.newLine();
                }
            }
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * Loads tasks from the configured data file.
     *
     * <p>Malformed records are skipped so that one bad line does not prevent
     * the rest of the task list from loading.</p>
     *
     * @return the restored task list, or an empty list if the file is unavailable
     */
    public TaskList load() {
        TaskList taskList = new TaskList();
        loadWarning = "";
        int skippedRecords = 0;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseTask(line);
                if (task == null) {
                    skippedRecords++;
                    continue;
                }
                try {
                    taskList.add(task);
                } catch (BaymaxException exception) {
                    skippedRecords++;
                }
            }
        } catch (NoSuchFileException exception) {
            return taskList;
        } catch (IOException exception) {
            loadWarning = "I have some concerns. I cannot read your care plan. "
                    + "Check the data file and its permissions, then restart. Saving is disabled to protect it.";
            return new TaskList();
        }
        if (skippedRecords > 0) {
            loadWarning = "I have some concerns. Skipped " + skippedRecords + " invalid or duplicate record(s). "
                    + "Repair the data file and restart. Saving is disabled to protect it.";
        }
        return taskList;
    }

    /** Returns any load warning, or an empty string when loading succeeded. */
    public String getLoadWarning() {
        return loadWarning;
    }

    /**
     * Parses one storage record, returning {@code null} for a malformed record.
     *
     * @param record the storage record to parse
     * @return the restored task, or {@code null} if the record is malformed
     */
    private Task parseTask(String record) {
        String[] fields = record.split(STORAGE_FIELD_SEPARATOR_REGEX, -1);
        if (fields.length < TODO_FIELD_COUNT
                || fields[DESCRIPTION_FIELD_INDEX].isBlank()
                || !hasValidCompletionStatus(fields)) {
            return null;
        }

        Task task;
        try {
            task = createTask(fields);
        } catch (DateTimeParseException | BaymaxException exception) {
            return null;
        }

        if (task == null) {
            return null;
        }
        if (fields[COMPLETION_STATUS_FIELD_INDEX].equals(COMPLETED_STATUS)) {
            task.markAsDone();
        }
        return task;
    }

    private boolean hasValidCompletionStatus(String[] fields) {
        String completionStatus = fields[COMPLETION_STATUS_FIELD_INDEX];
        return completionStatus.equals(COMPLETED_STATUS)
                || completionStatus.equals(INCOMPLETE_STATUS);
    }

    private Task createTask(String[] fields) {
        return switch (fields[TASK_TYPE_FIELD_INDEX]) {
            case TODO_TYPE -> fields.length == TODO_FIELD_COUNT
                    ? new Todo(fields[DESCRIPTION_FIELD_INDEX])
                    : null;
            case DEADLINE_TYPE -> fields.length == DEADLINE_FIELD_COUNT
                    ? new Deadline(
                            fields[DESCRIPTION_FIELD_INDEX],
                            LocalDate.parse(fields[PRIMARY_DATE_FIELD_INDEX]))
                    : null;
            case EVENT_TYPE -> fields.length == EVENT_FIELD_COUNT
                    ? new Event(
                            fields[DESCRIPTION_FIELD_INDEX],
                            LocalDate.parse(fields[PRIMARY_DATE_FIELD_INDEX]),
                            LocalDate.parse(fields[EVENT_END_DATE_FIELD_INDEX]))
                    : null;
            default -> null;
        };
    }
}
