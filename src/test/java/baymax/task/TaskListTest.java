package baymax.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list behavior that affects core task management features.
 */
public class TaskListTest {

    @Test
    public void find_keywordInTaskDescriptions_returnsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        tasks.add(new Event(
                "team meeting",
                LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4)));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] read book", matchingTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019)",
                matchingTasks.get(1).toString());
    }

    @Test
    public void find_keywordWithDifferentCapitalization_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(1, matchingTasks.size());
        assertEquals("[T][ ] Read Book", matchingTasks.get(0).toString());
    }
}
