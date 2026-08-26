import java.util.Scanner;
import java.util.ArrayList;

/**
 * Handles console input for Baymax.
 */
public class Ui {
    private final Scanner scanner = new Scanner(System.in);
    private static final String SEPARATOR =
        "____________________________________________________________";

    /**
     * Checks whether another command is available.
     *
     * @return true if another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one command from the user.
     *
     * @return the user's command
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Displays Baymax's welcome message.
     */
    public void showWelcome() {
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
    }

    /**
     * Displays a separator between commands.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    /**
     * Displays a storage error.
     */
    public void showSaveError() {
        System.out.println(
                "Can not save tasks list. Previous tasks list can not be retrieve.");
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks the tasks to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }
}