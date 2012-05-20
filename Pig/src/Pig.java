/**
 * 
 * @author Dave Matuszek.
 * @version May 13, 2010.
 */

public class Pig {
    /** Controls play of the game. Has players alternate moves, decides when
     * the game is over, and declares the winner.
     * @param args Not used.
     */
    public static void main(String [] args) {
        try {
            Human human = new Human();
            Computer computer = new Computer();
            printTheRules();
            System.out.println("\n---------- New Game ----------\n");
            while (human.score < 50 && computer.score < 50) {
                Thread.sleep(500);
                computer.score += computer.play(0);
                System.out.println("Computer's score is now " + computer.score + ".\n");
                if (computer.score > human.score) {
                    System.out.println("You need " + (computer.score - human.score) +
                    " to catch up.\n");
                }
                human.score += human.play(0);
                System.out.println("Your score is now " + human.score + ".\n");
            }
            Thread.sleep(2000);
            if (human.score > computer.score) System.out.println("*** You win!! ***");
            else if (human.score < computer.score) System.out.println("Too bad...you lose.");
            else System.out.println("Tie game.");
            System.out.println("\n");
        }
        catch (InterruptedException ex) {
            System.err.println("Ran into an unexpected error: " + ex);
        }
    }

    /** Prints the rules. */
    public static void printTheRules() {
        System.out.println("Welcome to the game of Pig!");
        System.out.println();
        System.out.println("The game is you vs. the computer; the computer plays first.");
        System.out.println("At each turn, you roll a six-sided die as many times as you");
        System.out.println("like. Those rolls get added to your score, unless you roll a");
        System.out.println("1, in which case your turn ends and you get nothing for that");
        System.out.println("turn.");
        System.out.println();
        System.out.println("The game ends when one or both players reach 50 points and");
        System.out.println("both players have had the same number of turns. Since the");
        System.out.println("computer plays first, if it reaches 50, you get one more turn.");
        System.out.println("High score wins.");
        System.out.println();
        System.out.println("Good luck!");
    }
}
