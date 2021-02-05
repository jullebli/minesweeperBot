
package minesweeper.bot;

import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Move;
import minesweeper.model.Square;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class FirstBotTest {
    private Bot bot;
    private MinefieldGenerator generator;
    private Board board;
    
        @Before
    public void setUp() {
        this.bot = BotSelect.getBot(); //returns SecondBot?
        this.generator = new MinefieldGenerator();
        board = new Board(new MinefieldGenerator(5), 30, 16, 99);
        generator.generate(board, 99, 1, 1);
    }
}
