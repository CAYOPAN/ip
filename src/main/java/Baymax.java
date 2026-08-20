import java.util.Scanner;

/**
 * Runs Baymax's text user interface.
 *
 * <p>Each line entered by the user is stored as a task, unless it is one of
 * the special commands {@code list}, {@code todo}, {@code deadline},
 * {@code event}, {@code mark}, {@code unmark}, or
 * {@code bye}. Tasks are kept only while the program is running, as required
 * for this level.</p>
 */
public class Baymax {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        System.out.print("""
                ____________________________________________________________
                BBBB   aaa   y   y  m     m   aaa   x   x
                B   B a   a  y   y  mm   mm  a   a  x   x
                B   B a   a   y y   m m m m  a   a   x x
                BBBB  aaaaa    y    m  m  m  aaaaa    x
                B   B a   a    y    m     m  a   a   x x
                B   B a   a    y    m     m  a   a  x   x
                BBBB  a   a    y    m     m  a   a  x   x
                Hello! I'm Baymax. Your personal task companion.
                What can I do for you?
                ____________________________________________________________
                """);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println("____________________________________________________________");
            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            }

            if (command.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                String taskNumberText = command.substring("mark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumberText) - 1;
                    if (taskIndex >= 0 && taskIndex < taskCount) {
                        tasks[taskIndex].markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   " + tasks[taskIndex]);
                    } else {
                        System.out.println(" Sorry, that task does not exist.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println(" Sorry, please provide a valid task number.");
                }
            } else if (command.startsWith("unmark ")) {
                String taskNumberText = command.substring("unmark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumberText) - 1;
                    if (taskIndex >= 0 && taskIndex < taskCount) {
                        tasks[taskIndex].markAsUndone();
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("   " + tasks[taskIndex]);
                    } else {
                        System.out.println(" Sorry, that task does not exist.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println(" Sorry, please provide a valid task number.");
                }
            } else if (command.startsWith("todo ")) {
                String description = command.substring("todo".length()).trim();
                if (description.isEmpty()) {
                    System.out.println(" Sorry, a todo must have a description.");
                } else if (taskCount < MAX_TASKS) {
                    tasks[taskCount] = new Todo(description);
                    taskCount++;
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks[taskCount - 1]);
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
                } else {
                    System.out.println(" Sorry, your task list is full.");
                }
            } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                String deadlineDetails = command.substring("deadline".length()).trim();
                int byMarkerIndex = deadlineDetails.indexOf("/by");
                String description = byMarkerIndex < 0
                        ? ""
                        : deadlineDetails.substring(0, byMarkerIndex).trim();
                String by = byMarkerIndex < 0
                        ? ""
                        : deadlineDetails.substring(byMarkerIndex + "/by".length()).trim();

                if (description.isEmpty() || by.isEmpty()) {
                    System.out.println(" Sorry, a deadline needs a description and a due date.");
                } else if (taskCount < MAX_TASKS) {
                    tasks[taskCount] = new Deadline(description, by);
                    taskCount++;
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks[taskCount - 1]);
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
                } else {
                    System.out.println(" Sorry, your task list is full.");
                }
            } else if (command.equals("event") || command.startsWith("event ")) {
                String eventDetails = command.substring("event".length()).trim();
                int fromMarkerIndex = eventDetails.indexOf("/from");
                int toMarkerIndex = fromMarkerIndex < 0
                        ? -1
                        : eventDetails.indexOf("/to", fromMarkerIndex + "/from".length());
                String description = fromMarkerIndex < 0
                        ? ""
                        : eventDetails.substring(0, fromMarkerIndex).trim();
                String from = fromMarkerIndex < 0 || toMarkerIndex < 0
                        ? ""
                        : eventDetails.substring(fromMarkerIndex + "/from".length(), toMarkerIndex).trim();
                String to = toMarkerIndex < 0
                        ? ""
                        : eventDetails.substring(toMarkerIndex + "/to".length()).trim();

                if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                    System.out.println(" Sorry, an event needs a description, start time, and end time.");
                } else if (taskCount < MAX_TASKS) {
                    tasks[taskCount] = new Event(description, from, to);
                    taskCount++;
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks[taskCount - 1]);
                    System.out.println(" Now you have " + taskCount + " tasks in the list.");
                } else {
                    System.out.println(" Sorry, your task list is full.");
                }
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println(" added: " + command);
            } else {
                System.out.println(" Sorry, your task list is full.");
            }

            System.out.println("____________________________________________________________");
        }
    }
}
