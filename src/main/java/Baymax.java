import java.util.Scanner;

/**
 * Reads commands from the user, echoes them, and exits when the user enters
 * {@code bye}.
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
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println("____________________________________________________________");
            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            }

            System.out.println(" " + command);
            System.out.println("____________________________________________________________");
        }
    }
}
