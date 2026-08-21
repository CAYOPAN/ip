import java.util.ArrayList;
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
        ArrayList<Task> tasks = new ArrayList<>();

        while (scanner.hasNextLine()) {
            try {
                String command = scanner.nextLine();

                System.out.println("____________________________________________________________");
                if (command.equals("bye")) {
                    System.out.println(" Bye. Hope to see you again soon!");
                    break;
                }

                if (command.equals("list")) {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                } else if (command.startsWith("mark ")) {
                    String taskNumberText = command.substring("mark ".length()).trim();
                    try {
                        int taskIndex = Integer.parseInt(taskNumberText) - 1;
                        if (taskIndex >= 0 && taskIndex < tasks.size()) {
                            tasks.get(taskIndex).markAsDone();
                            System.out.println(" Nice! I've marked this task as done:");
                            System.out.println("   " + tasks.get(taskIndex));
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
                        if (taskIndex >= 0 && taskIndex < tasks.size()) {
                            tasks.get(taskIndex).markAsUndone();
                            System.out.println(" OK, I've marked this task as not done yet:");
                            System.out.println("   " + tasks.get(taskIndex));
                        } else {
                            System.out.println(" Sorry, that task does not exist.");
                        }
                    } catch (NumberFormatException exception) {
                        System.out.println(" Sorry, please provide a valid task number.");
                    }
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    String taskNumberText = command.equals("delete")
                            ? ""
                            : command.substring("delete ".length()).trim();
                    try {
                        int taskIndex = Integer.parseInt(taskNumberText) - 1;
                        if (taskIndex >= 0 && taskIndex < tasks.size()) {
                            Task removedTask = tasks.remove(taskIndex);
                            System.out.println(" Noted. I've removed this task:");
                            System.out.println("   " + removedTask);
                            System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                        } else {
                            System.out.println(" Sorry, that task does not exist.");
                        }
                    } catch (NumberFormatException exception) {
                        System.out.println(" Sorry, please provide a valid task number.");
                    }
                } else if (command.startsWith("todo ")) {
                    String description = command.substring("todo".length()).trim();
                    if (description.isEmpty()) {
                        throw new EmptyDescriptionException("todo");
                    } else {
                        tasks.add(new Todo(description));
                        System.out.println(" Got it. I've added this task:");
                        System.out.println("   " + tasks.get(tasks.size() - 1));
                        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    }
                } else if (command.startsWith("deadline ")) {
                    String deadlineDetails = command.substring("deadline".length()).trim();
                    int byMarkerIndex = deadlineDetails.indexOf("/by");
                    String description = byMarkerIndex < 0
                            ? ""
                            : deadlineDetails.substring(0, byMarkerIndex).trim();
                    String by = byMarkerIndex < 0
                            ? ""
                            : deadlineDetails.substring(byMarkerIndex + "/by".length()).trim();

                    if (byMarkerIndex < 0 || by.isEmpty()) {
                        throw new EmptyByException();
                    } else if (description.isEmpty()) {
                        throw new EmptyDescriptionException("deadline");
                    } else {
                        tasks.add(new Deadline(description, by));
                        System.out.println(" Got it. I've added this task:");
                        System.out.println("   " + tasks.get(tasks.size() - 1));
                        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    }
                } else if (command.startsWith("event ")) {
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

                    if (description.isEmpty()) {
                        throw new EmptyDescriptionException("event");
                    } else if (from.isEmpty()) {
                        throw new EmptyFromException();
                    } else if (to.isEmpty()) {
                        throw new EmptyToException();
                    } else {
                        tasks.add(new Event(description, from, to));
                        System.out.println(" Got it. I've added this task:");
                        System.out.println("   " + tasks.get(tasks.size() - 1));
                        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    }
                } else {
                    throw new InvalidCommandException();
                }
            } catch (BaymaxException e) {
                System.out.println(e.getMessage());
            } finally {
                System.out.println("____________________________________________________________");
            }

        }
    }
}
