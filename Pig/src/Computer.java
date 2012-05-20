
/** The computer player for the game of Pig. */
public class Computer extends Player {
    public Computer() {
        who = "Computer";
    }

    /** Lets the computer decide whether to roll again.
     * @param scoreIncrement  The amount gained so far this turn.
     * @return Whether to roll again. */
    public boolean playAgainTest(int scoreIncrement) {
        return scoreIncrement < 17;
    }
} // end of Computer class
