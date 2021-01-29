
package minesweeper.bot;

import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import org.junit.Before;

public class FirstBotTest {
    private Bot bot;
    private MinefieldGenerator generator;
    private Board board;
    @Before
    public void setUp() {
        this.bot = BotSelect.getBot();
        this.generator = new MinefieldGenerator();
        this.board = new Board(generator, 10, 10, 3);
    }
}
