
package minesweeper.bot;

import java.util.Random;
import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Move;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class SinglePointBotTest {
    private SinglePointBot bot;
    private Board board;

    @Before
    public void setUp() {
        this.bot = new SinglePointBot();
    }

    public boolean playGame(Bot bot, Board board) {
        while (!board.gameLost && !board.gameWon) {
            Move move = bot.makeMove(board);
            board.makeMove(move);
        }
        return board.gameWon;
    }

    @Test
    public void solvesBoardWithFiveMines() {
        MinefieldGenerator generator = new MinefieldGenerator(20);
        board = new Board(generator, 10, 10, 5);
        assertTrue(playGame(bot, board));
    }

    @Test
    public void doesNotSolveHardBoard() {
        MinefieldGenerator generator = new MinefieldGenerator(20);
        board = new Board(generator, 10, 10, 25);
        assertFalse(playGame(bot, board));
    }

    @Test
    public void losingMoveIsAlwaysRandom() {
        for (int i = 0; i < 100; i++) {
            Random rng = new Random(i);
            MinefieldGenerator generator = new MinefieldGenerator(rng.nextInt());
            int width = 5 + rng.nextInt(20);
            int height = 5 + rng.nextInt(20);
            int mines = rng.nextInt((int) (width * height * 0.9));
            board = new Board(generator, width, height, mines);
            if (board.gameLost) {
                assertTrue(bot.prevMoveWasRandom());
            }
        }
    }
}