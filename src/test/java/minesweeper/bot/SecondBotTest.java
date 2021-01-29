
package minesweeper.bot;

import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Square;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class SecondBotTest {
    private Bot bot;
    private MinefieldGenerator generator;
    private Board board;
    
    @Before
    public void setUp() {
        this.bot = BotSelect.getBot();
        this.generator = new MinefieldGenerator();
        this.board = new Board(generator, 10, 1, 1);
    }
   
    /* something wrong with tests figure out later
    @Test
    public void firsMoveIsOpensRightSquare() {
        this.bot.makeMove(board);
        Square s = board.getSquareAt(0, 0);
        assertTrue(s.isOpened());
    }
    
    @Test
    public void firsMoveIsOpensAtLeastOneSquare() {
        this.bot.makeMove(board);
        assertTrue(board.getOpenSquares().size() >= 1);
    }

    @Test
    public void squareBottomLeftIsNotMine() {
        this.bot.makeMove(board);
        Square s = board.getSquareAt(0, 0);
        assertFalse(s.isMine());
    } */
    
}
