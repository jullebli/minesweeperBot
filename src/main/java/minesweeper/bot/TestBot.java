
package minesweeper.bot;

import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Highlight;
import minesweeper.model.Pair;
import minesweeper.model.Square;


/**
 * A basic bot template for testing purposes and as an example
 *
 * <p>
 * This class is meant to demonstrate the basic outline of a bot implementation
 * and isn't itself a very good Minesweeper bot. Basically, this implementation
 * can work as a template for Data Structures and Algorithms projects for actual
 * Bot implementation.
 * </p>
 *
 * <p>
 * The Bot will be called externally (the interface for this is the makeMove()
 * function) and be given the current game state represented by a Board object.
 * <b>THE BOT DOES NOT MODIFY THIS BOARD OBJECT.</b> Instead the bot needs to
 * determine the best action to take by returning a Move object, which represents
 * one action that can be executed onto the board. Refer to model/Move.java for
 * details.
 * </p>
 *
 * <p>
 * For Data Structures and Algorithms, you can copy this bot implementation into
 * your own class (e.g. MyBot.java) and implement your AI algorithm within the
 * makeMove() method.
 * </p>
 */
public class TestBot implements Bot {

    private Random rng = new Random();
    private GameStats gameStats;

    /**
     * Make a single decision based on the given Board state
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove(Board board) {
        // Find the coordinate of an unopened square
        Pair<Integer> pair = findUnopenedSquare(board);
        int x = (int) pair.first;
        int y = (int) pair.second;

        // The TestBot isn't very smart and randomly
        // decides what move should be made using java.util.Random
        Integer type = rng.nextInt(10);

        // Certain move types are given more weight
        // but these moves are still extremely random
        // and most likely not correct
        if (type < 5) {
            return new Move(MoveType.OPEN, x, y);
        } else if (type < 8) {
            return new Move(MoveType.FLAG, x, y);
        } else {
            if (rng.nextInt(2) == 0) {
                return new Move(x, y, Highlight.GREEN);
            } else {
                return new Move(x, y, Highlight.RED);
            }
        }
    }
    /**
     * Return multiple possible moves to make based on current board state.
     * Suggested to be used for a "helper" bot to provide multiple highlights at once.
     * @param board The current board state.
     * @return List of moves for current board.
     */
    @Override
    public ArrayList<Move> getPossibleMoves(Board board) {
        ArrayList<Move> movesToMake = new ArrayList<>();
        HashSet<Pair<Integer>> pairs = new HashSet();

        //Chooses a random amount of moves to make between 1 and total number of mines
        int movesToReturn = rng.nextInt(board.totalMines) + 1;

        for (int i = 0; i < movesToReturn; i++) {
            Boolean found = false;
            Pair<Integer> pair = new Pair(-1, -1);
            //Attempts to find a unique unopened square up to 5 times or until it is successfully found
            for (int attempt = 0; attempt < 6 && !found; attempt++) {
                pair = findUnopenedSquare(board);
                if (!pairs.contains(pair)) {
                    pairs.add(pair);
                    found = true;
                } 
            }
           
            if (found) {
                if (i < Math.floor(movesToReturn / 2)) {
                    movesToMake.add(new Move(pair.first, pair.second, Highlight.GREEN));
                } else {
                    movesToMake.add(new Move(pair.first, pair.second, Highlight.RED));
                }
            } else {
                //if a square is not found, skips the rest of the for loop
                i = movesToReturn;
            }
        }
        return movesToMake;
    }

    /**
     * Used to pass the bot the gameStats object, useful for tracking previous moves
     */
    @Override
    public void setGameStats(GameStats gameStats) {
        this.gameStats = gameStats;
    }

    /**
     * Find the (X, Y) coordinate pair of an unopened square
     * from the current board
     * @param board The current board state
     * @return An (X, Y) coordinate pair
     */
    public Pair<Integer> findUnopenedSquare(Board board) {
        Boolean unOpenedSquare = false;

        // board.getOpenSquares allows access to already opened squares
        HashSet<Square> opened = board.getOpenSquares();
        int x;
        int y;

        Pair<Integer> pair = new Pair<>(0, 0);

        // Randomly generate X,Y coordinate pairs that are not opened
        while (!unOpenedSquare) {
            x = rng.nextInt(board.width);
            y = rng.nextInt(board.height);
            if (!opened.contains(board.board[x][y])) {
                unOpenedSquare = true;
                pair = new Pair<Integer>(x, y);
            }
        }

        // This pair should point to an unopened square now
        return pair;
    } 

}