import java.util.Scanner;

/**
 * Runs Baymax's text user interface.
 *
 * <p>Each line entered by the user is stored as a task, unless it is one of
 * the special commands {@code list}, {@code mark}, {@code unmark}, or
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
                    System.out.println(" " + (i + 1) + ".[" + tasks[i].getStatusIcon() + "] "
                            + tasks[i].getDescription());
                }
            } else if (command.startsWith("mark ")) {
                String taskNumberText = command.substring("mark ".length()).trim();
                try {
                    int taskIndex = Integer.parseInt(taskNumberText) - 1;
                    if (taskIndex >= 0 && taskIndex < taskCount) {
                        tasks[taskIndex].markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   [X] " + tasks[taskIndex].getDescription());
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
                        System.out.println("   [ ] " + tasks[taskIndex].getDescription());
                    } else {
                        System.out.println(" Sorry, that task does not exist.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println(" Sorry, please provide a valid task number.");
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
