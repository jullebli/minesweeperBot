package minesweeper.bot;

import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Highlight;
import minesweeper.model.Pair;
import minesweeper.model.Square;

public class SecondBot implements Bot {

    private Random rng = new Random();
    private GameStats gameStats;
    private boolean firstMove = true;
    private Set<Square> setS = new HashSet<>();
    private Set<Square> setQ = new HashSet<>();
    private Set<Square> marked = new HashSet<>();
    private Square prev; //most recently opened square

    /**
     * Make a single decision based on the given Board state
     *
     * @param board The current board state.
     * @return Move to be made onto the board.
     */
    @Override
    public Move makeMove(Board board) {
        if (firstMove) {
            firstMove = false;
            prev = board.getSquareAt(0, 0);
            System.out.println("First move");
            return new Move(MoveType.OPEN, 0, 0);
        }

        if (isAFN(prev, board)) {
            setS.addAll(getUnmarkedNeighbours(prev, board));
        } else {
            setQ.add(prev);
        }

        if (!setS.isEmpty()) {
            prev = setS.iterator().next();
            setS.remove(prev);
            System.out.println("move from S");
            return new Move(MoveType.OPEN, prev.getX(), prev.getY());
        }

        Iterator<Square> it = setQ.iterator();
        while (it.hasNext()) {
            Square q = it.next();
            if (isAMN(q, board)) {
                for (Square y : getUnmarkedNeighbours(q, board)) {
                    marked.add(y);
                }
                setQ.remove(q);
            }
        }

        for (Square q : setQ) {
            if (isAFN(q, board)) {
                setS.addAll(getUnmarkedNeighbours(q, board));
                setQ.remove(q);
            }
        }

        if (setS.isEmpty()) {
            //Here should be the third algorithm CSP? Now just random square
            System.out.println("Picking random (S is empty)");
            Pair<Integer> pair = findUnopenedSquare(board);
            int x = (int) pair.first;
            int y = (int) pair.second;

            prev = board.getSquareAt(x, y);
            return new Move(MoveType.OPEN, x, y);
        }
        
        System.out.println("Calling makeMove from MakeMove");
        return makeMove(board);
    }

    /**
     * Return multiple possible moves to make based on current board state.
     * Suggested to be used for a "helper" bot to provide multiple highlights at
     * once.
     *
     * @param board The current board state.
     * @return List of moves for current board.
     */
    @Override
    public ArrayList<Move> getPossibleMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        for (Square square : setS) {
            moves.add(new Move(square.getX(), square.getY(), Highlight.GREEN));
        }
        return moves;
    }

    /**
     * Determine if an opened square fills in the AFN terms.
     * In the case of AFN (All free neighbours) the square has as many
     * flagged neighbours as it's number. So all other neighbours except
     * the flagged ones are safe to open.
     * 
     * @param square the square which neighbours are examined.
     * @param board The current board state.
     * @return if square is an AFN case.
     */
    private boolean isAFN(Square square, Board board) {
        int flaggedNeighbours = 0;
        int unopenedNeighbours = 0;

        for (int xInc = -1; xInc <= 1; xInc++) {
            for (int yInc = -1; yInc <= 1; yInc++) {
                if (xInc == 0 && yInc == 0) {
                    continue;
                }
                if (board.withinBoard(square.getX() + xInc, square.getY() + yInc)) {
                    Square s = board.getSquareAt(square.getX() + xInc, square.getY() + yInc);
                    if (!s.isOpened() && isFlagged(s)) {
                        flaggedNeighbours++;
                    } else if (!s.isOpened()) {
                        unopenedNeighbours++;
                    }
                }
            }
        }
        return square.surroundingMines() == flaggedNeighbours
                && unopenedNeighbours > 0;
    }

    /**
     * /**
     * Determine if an opened square fills in the AMN terms.
     * In the case of AMN (All mine/marked neighbours) the square has as many
     * unopened neighbours as it's number. So all of it's neighbours are mines.
     * 
     * @param square the square which neighbours are examined.
     * @param board the current board state.
     * @return if square is an AMN case.
     */
    private boolean isAMN(Square square, Board board) {
        Set<Square> unmarkedNeighbours = getUnmarkedNeighbours(square, board);
        return square.surroundingMines() == unmarkedNeighbours.size();
    }

    /**
     * Get the square's all unopened unmarked neighbours as a set.
     * 
     * @param square the square which neighbours are examined
     * @param board the current board state
     * @return 
     */
    private Set<Square> getUnmarkedNeighbours(Square square, Board board) {
        Set<Square> neighbours = new HashSet<>();

        for (int xInc = -1; xInc <= 1; xInc++) {
            for (int yInc = -1; yInc <= 1; yInc++) {
                if (xInc == 0 && yInc == 0) {
                    continue;
                }
                int x = square.getX() + xInc;
                int y = square.getY() + yInc;
                if (board.withinBoard(square.getX() + xInc, square.getY() + yInc)) {
                    Square s = board.getSquareAt(x, y);
                    if (!s.isOpened() && !isFlagged(s)) {
                        neighbours.add(s);
                    }
                }
            }
        }
        return neighbours;
    }
    
    /**
     * Find out if a square is in the marked set.
     * 
     * @param square a Square variable
     * @return if the square is in the marked set.
     */
    private boolean isFlagged(Square square) {
        return marked.contains(square);
    }

    /**
     * Used to pass the bot the gameStats object, useful for tracking previous
     * moves
     */
    @Override
    public void setGameStats(GameStats gameStats) {
        this.gameStats = gameStats;
    }

    /**
     * Find the (X, Y) coordinate pair of an random unopened
     * square from the current
     * board
     *
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
            if (!opened.contains(board.board[x][y]) && !isFlagged(board.getSquareAt(x, y))) {
                unOpenedSquare = true;
                pair = new Pair<Integer>(x, y);
            }
        }

        // This pair should point to an unopened square now
        return pair;
    }
}
