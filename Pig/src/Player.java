import java.util.Random;

/** Describes a player of the game of Pig. Used mainly to roll the die
 * and to keep track of scores. */
public abstract class Player {

    /** Random number generator. */
    public Random random = new Random();
    /** Name of the player ("You" or "Computer"). */
    public String who;
    /** The player's current score. */
    public int score = 0;

    /** Returns a random integer in the range 1 to 6, inclusive.
     * @param who Which player is rolling the die (for printing purposes).
     * @return The result of a die roll. */
    public int rollDie(String who) throws InterruptedException {
        Thread.sleep(1000);
        int roll = random.nextInt(6) + 1;
        System.out.print(who + " rolled a " + roll + "; ");
        if (roll == 1) System.out.println("that ends the turn.");
        return roll;
    }

    /** Plays out one turn, by rolling the die some number of times. The final
     * score is the sum of all the rolls, except that a roll of 1 terminates
     * the turn and gives a final score of zero.
     * @param previousScore The accumulated points so far this turn.
     * @return The final score at the end of this turn. */
    public int play(int previousScore) throws InterruptedException {
        if (playAgainTest(previousScore)) {
            int roll = rollDie(who);
            if (roll == 1) return 0;
            else {
                System.out.println("so far this turn: " + (previousScore + roll) + ".");
                return play(previousScore + roll);
            }
        }
        else return previousScore;
    }

    /** Abstract test used to determine whether to roll again.
     * @param n The points accumulated so far this turn.
     * @return Whether to roll again. */
    public abstract boolean playAgainTest(int n);

} // end of Player class

