import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** The human player for the game of Pig. */
public class Human extends Player {
    public Human() {
        who = "You";
    }
    /** Lets the user decide whether to roll again.
     * @param scoreIncrement  The amount gained so far this turn (unused).
     * @return Whether to roll again. */
    public boolean playAgainTest(int scoreIncrement) {
        return askToRollAgain();
    }

    /**
     * Asks the user whether to roll again.
     * @return true if the user wants to roll again. */
    public boolean askToRollAgain() {
        try {
            return answer("Roll?");
        }
        catch (IOException ex) {
            System.err.println("Encountered an unexpected error: " + ex);
            System.exit(1);
            return false;
        }
    }

    /** Prints the question, then accepts input from the user until one of
     * the characters 'Y', 'y' (yes), 'N', 'n' (no), or 'Q' or 'q' (quit).
     * @param question The binary question to ask the user.
     * @return `true` for yes, `false` for no, or
     * exits the program for quit. */
    public boolean answer(String question) throws IOException {
        System.out.print(question + " ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = br.readLine();
        if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("n")) return input.equalsIgnoreCase("y");
        else if (input.equalsIgnoreCase("q")) {
            System.out.println("Goodbye!");
            System.exit(0);
            return false;
        }
        else return answer(question);
    }
} // end of Human class

