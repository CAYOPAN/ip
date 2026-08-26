package baymax.storage;

import baymax.task.Deadline;
import baymax.task.Event;
import baymax.task.Task;
import baymax.task.TaskList;
import baymax.task.Todo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Loads tasks from and saves tasks to Baymax's data file.
 */
public class Storage {
    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

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

    public TaskList load() {
        ArrayList<Task> taskList = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String[] fields = scanner.nextLine().split("\\s*\\|\\s*", -1);
                if (fields.length < 3) {
                    continue;
                }

                boolean isDone;
                if (fields[1].equals("1")) {
                    isDone = true;
                } else if (fields[1].equals("0")) {
                    isDone = false;
                } else {
                    continue;
                }

                Task task;
                try {
                    task = switch (fields[0]) {
                    case "T" -> fields.length == 3 ? new Todo(fields[2]) : null;
                    case "D" -> fields.length == 4
                            ? new Deadline(fields[2], LocalDate.parse(fields[3])) : null;
                    case "E" -> fields.length == 5
                            ? new Event(fields[2], LocalDate.parse(fields[3]), LocalDate.parse(fields[4])) : null;
                    default -> null;
                    };
                } catch (DateTimeParseException exception) {
                    continue;
                }

                if (task == null) {
                    continue;
                }
                if (isDone) {
                    task.markAsDone();
                }
                taskList.add(task);
            }
        } catch (IOException io) {
            return new TaskList();
        }

        return new TaskList(taskList);
    }
}
