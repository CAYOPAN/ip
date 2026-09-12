package baymax.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

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

    /**
     * Creates a storage helper that reads from and writes to the given file path.
     *
     * @param filePath the data file path used to persist tasks
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves all tasks to the configured data file, creating parent folders if needed.
     *
     * @param taskList the tasks to save
     * @throws IOException if the file cannot be written
     */
    public void save(TaskList taskList) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                fileWriter.write(task.toStorageString());
                fileWriter.write(System.lineSeparator());
            }
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
        ArrayList<Task> taskList = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                Task task = parseTask(scanner.nextLine());
                if (task != null) {
                    taskList.add(task);
                }
            }
        } catch (IOException exception) {
            return new TaskList();
        }

        return new TaskList(taskList);
    }

    /**
     * Parses one storage record, returning {@code null} for a malformed record.
     *
     * @param record the storage record to parse
     * @return the restored task, or {@code null} if the record is malformed
     */
    private Task parseTask(String record) {
        String[] fields = record.split(STORAGE_FIELD_SEPARATOR_REGEX, -1);
        if (fields.length < TODO_FIELD_COUNT || !hasValidCompletionStatus(fields)) {
            return null;
        }

        Task task;
        try {
            task = createTask(fields);
        } catch (DateTimeParseException exception) {
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
