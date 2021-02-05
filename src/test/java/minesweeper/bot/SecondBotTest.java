package minesweeper.bot;

import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Move;
import minesweeper.model.Square;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;

public class SecondBotTest {

    private Bot bot;
    private MinefieldGenerator generator;
    private Board board;

    @Before
    public void setUp() {
        this.bot = BotSelect.getBot();
        this.generator = new MinefieldGenerator();
        board = new Board(new MinefieldGenerator(5), 30, 16, 99);
        generator.generate(board, 99, 1, 1);
    }

    @Test
    public void firsMoveIsOpensRightSquare() {
        Move move = this.bot.makeMove(this.board);
        this.board.makeMove(move);
        Square s = board.getSquareAt(0, 0);
        assertTrue(s.isOpened());
    }

    @Test
    public void firsMoveOpensAtLeastOneSquare() {
        Move move = this.bot.makeMove(this.board);
        this.board.makeMove(move);
        assertTrue(board.getOpenSquares().size() >= 1);
    }

    @Test
    public void squareBottomLeftIsNotMine() {
        Move move = this.bot.makeMove(this.board);
        this.board.makeMove(move);
        Square s = board.getSquareAt(0, 0);
        assertFalse(s.isMine());
    }
    
    @Test
    public void secondMoveIsRight() {
        Move move = this.bot.makeMove(this.board);
        this.board.makeMove(move);
        Move move2 = this.bot.makeMove(this.board);
        this.board.makeMove(move);
        //assertFalse(s.isMine());
    }

    @Test
    public void printBoard() {
        //the board changes sometimes even though I use a seed
        Move move = this.bot.makeMove(this.board);
        this.board.makeMove(move);
        System.out.println(board.toString());
        Move move2 = this.bot.makeMove(this.board);
        this.board.makeMove(move2);
        System.out.println(board.toString());
        Move move3 = this.bot.makeMove(this.board);
        this.board.makeMove(move3);
        System.out.println(board.toString());
        Move move4 = this.bot.makeMove(this.board);
        this.board.makeMove(move4);
        System.out.println(board.toString());
        Move move5 = this.bot.makeMove(this.board);
        this.board.makeMove(move5);
        System.out.println(board.toString());
        
    }
}
