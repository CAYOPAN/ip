import java.util.Scanner;

/**
 * Handles console input for Baymax.
 */
public class Ui {
    private final Scanner scanner = new Scanner(System.in);

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
}