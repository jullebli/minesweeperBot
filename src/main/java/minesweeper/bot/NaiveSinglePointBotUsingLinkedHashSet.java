
package minesweeper.bot;

import java.util.Random;
import java.util.ArrayList; //getPossibleMoves needs to return an ArrayList
import java.util.LinkedHashSet;
import minesweeper.util.MyList;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Highlight;
import minesweeper.model.Pair;
import minesweeper.model.Square;

/**
 * A bot that is same as the NaiveSinglePointBot but uses LinkedHashSet instead of Myset.
 *
 * @see "Becerra, David J. 2015. Algorithmic Approaches to PlayingMinesweeper.
 * Bachelor's thesis, Harvard College. Link:
 * <a href=http://nrs.harvard.edu/urn-3:HUL.InstRepos:14398552>Becerra's
 * thesis</a>"
 *
 */
public class NaiveSinglePointBotUsingLinkedHashSet implements Bot {

    private Random rng = new Random(1);
    private GameStats gameStats;
    private boolean firstMove = true;
    private LinkedHashSet<Square> setS = new LinkedHashSet<>();
    private LinkedHashSet<Square> marked = new LinkedHashSet<>();
    private LinkedHashSet<Square> opened = new LinkedHashSet<>();
    private MyList<Square> flagsToDraw = new MyList<>();
    private Square prev; //most recently opened square
    private boolean prevOpenedFirstTime = false;
    private boolean prevMoveWasRandom = false;

    /**
     * Make a single decision based on the given Board state.
     *
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove(Board board) {
        prevMoveWasRandom = false;
        if (!flagsToDraw.isEmpty()) {
            Square s = flagsToDraw.get(0);
            flagsToDraw.remove(0);
            return new Move(MoveType.FLAG, s.getX(), s.getY());
        }

        if (!firstMove) {
            if (isAFN(prev, board)) {
                setS.addAll(getUnmarkedNeighbours(prev, board));
            } else if (isAMN(prev, board)) {

                for (Square y : getUnmarkedNeighbours(prev, board)) {
                    if (!isMarked(y)) {
                        //adding new flag -> add opened neighbours of marked square
                        setS.addAll(getOpenedNeighbours(y, board));
                        marked.add(y);
                        flagsToDraw.add(y);
                    }
                }
            } else if (prevOpenedFirstTime) {
                //opening a aquare for the first time might give information to
                //neighbours, add them to S and evaluate(isAFN,isAMN) them again
                setS.addAll(getOpenedNeighbours(prev, board));
            }
        } else {
            firstMove = false;
        }

        if (setS.isEmpty()) {
            if (!flagsToDraw.isEmpty()) {
                Square s = flagsToDraw.get(0);
                flagsToDraw.remove(0);
                return new Move(MoveType.FLAG, s.getX(), s.getY());
            }

            Pair<Integer> pair = findUnopenedSquare(board);
            int x = (int) pair.first;
            int y = (int) pair.second;

            setS.add(board.getSquareAt(x, y));
            prevMoveWasRandom = true;
        }

        prev = setS.iterator().next();
        prevOpenedFirstTime = !opened.contains(prev);
        setS.remove(prev);
        return openMove(prev);
    }

    /**
     * Return multiple possible moves to make based on the current board state.
     *
     * @param board The current board state.
     * @return List of moves for current board.
     */
    @Override
    public ArrayList<Move> getPossibleMoves(Board board) {
        ArrayList<Move> arrayList = getPossibleMovesAsMyList(board).toArrayList();
        return arrayList;
    }

    /**
     * Return multiple possible moves to make based on current board state.
     *
     * @param board The current board state.
     * @return List of moves for current board.
     */
    public MyList<Move> getPossibleMovesAsMyList(Board board) {
        setS = new LinkedHashSet<>();
        marked = new LinkedHashSet<>();
        opened = new LinkedHashSet<>();

        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                Square s = board.getSquareAt(x, y);
                if (s.isOpened()) {
                    opened.add(s);
                } else if (s.isFlagged()) {
                    marked.add(s);
                }
            }
        }

        for (Square s : board.getOpenSquares()) {
            if (isAFN(s, board)) {
                setS.addAll(getUnmarkedNeighbours(s, board));
            } else if (isAMN(s, board)) {
                marked.addAll(getUnmarkedNeighbours(s, board));
            }
        }

        MyList<Move> moves = new MyList<>();

        for (Square square : setS) {
            moves.add(new Move(square.getX(), square.getY(), Highlight.GREEN));
        }
        
        for (Square square : marked) {
            if (!square.isFlagged()) {
                moves.add(new Move(square.getX(), square.getY(), Highlight.RED));
            }
        }
        return moves;
    }

    /**
     * Determine if a specified opened square fulfills the AFN terms. In the
     * case of AFN (All Free Neighbours) the square has as many marked/flagged
     * neighbours as its mine count number. So all other neighbours except the
     * marked/flagged ones are safe to open.
     *
     * @param square the square which neighbours are examined
     * @param board The current board state
     * @return true if the specified square is an AFN case
     */
    private boolean isAFN(Square square, Board board) {
        int markedNeighbours = 0;

        for (int xInc = -1; xInc <= 1; xInc++) {
            for (int yInc = -1; yInc <= 1; yInc++) {
                if (xInc == 0 && yInc == 0) {
                    continue;
                }
                if (board.withinBoard(square.getX() + xInc, square.getY() + yInc)) {
                    Square s = board.getSquareAt(square.getX() + xInc, square.getY() + yInc);
                    if (!s.isOpened() && isMarked(s)) {
                        markedNeighbours++;
                    }
                }
            }
        }
        return square.surroundingMines() == markedNeighbours;
    }

    /**
     * Determine if a specified opened square fulfills the AMN terms. In the
     * case of AMN (All mine/marked neighbours) the square has as many unopened
     * neighbours as it's mine count number. So all of it's unopened neighbours
     * are mines.
     *
     * @param square the square which neighbours are examined
     * @param board the current board state
     * @return true if the specified square is an AMN case
     */
    private boolean isAMN(Square square, Board board) {
        LinkedHashSet<Square> markedNeighbours = getMarkedNeighbours(square, board);
        return square.surroundingMines() - markedNeighbours.size()
                == getUnmarkedNeighbours(square, board).size();
    }

    /**
     * Returns a set containing all unopened unmarked neighbours of a specified
     * square.
     *
     * @param square the square which neighbours are examined
     * @param board the current board state
     * @return set containing all unopened unmarked neighbours of the specified
     * square
     */
    private LinkedHashSet<Square> getUnmarkedNeighbours(Square square, Board board) {
        LinkedHashSet<Square> neighbours = new LinkedHashSet<>();

        for (int xInc = -1; xInc <= 1; xInc++) {
            for (int yInc = -1; yInc <= 1; yInc++) {
                if (xInc == 0 && yInc == 0) {
                    continue;
                }
                int x = square.getX() + xInc;
                int y = square.getY() + yInc;
                if (board.withinBoard(square.getX() + xInc, square.getY() + yInc)) {
                    Square s = board.getSquareAt(x, y);
                    if (!opened.contains(s) && !isMarked(s)) {
                        neighbours.add(s);
                    }
                }
            }
        }
        return neighbours;
    }

    /**
     * Returns a set containing all marked neighbours of a specified square.
     *
     * @param square the square which neighbours are examined
     * @param board the current board state
     * @return set containing all marked neighbours of the specified square
     */
    private LinkedHashSet<Square> getMarkedNeighbours(Square square, Board board) {
        LinkedHashSet<Square> neighbours = new LinkedHashSet<>();

        for (int xInc = -1; xInc <= 1; xInc++) {
            for (int yInc = -1; yInc <= 1; yInc++) {
                if (xInc == 0 && yInc == 0) {
                    continue;
                }
                int x = square.getX() + xInc;
                int y = square.getY() + yInc;
                if (board.withinBoard(square.getX() + xInc, square.getY() + yInc)) {
                    Square s = board.getSquareAt(x, y);
                    if (!opened.contains(s) && isMarked(s)) {
                        neighbours.add(s);
                    }
                }
            }
        }
        return neighbours;
    }

    private LinkedHashSet<Square> getOpenedNeighbours(Square square, Board board) {
        LinkedHashSet<Square> neighbours = new LinkedHashSet<>();

        for (int xInc = -1; xInc <= 1; xInc++) {
            for (int yInc = -1; yInc <= 1; yInc++) {
                if (xInc == 0 && yInc == 0) {
                    continue;
                }
                int x = square.getX() + xInc;
                int y = square.getY() + yInc;
                if (board.withinBoard(square.getX() + xInc, square.getY() + yInc)) {
                    Square s = board.getSquareAt(x, y);
                    if (s.isOpened()) {
                        neighbours.add(s);
                    }
                }
            }
        }
        return neighbours;
    }

    /**
     * Returns true if the specified square is in the marked set.
     *
     * @param square a Square variable
     * @return true if the specified square is in the marked set.
     */
    private boolean isMarked(Square square) {
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
     * Find the (X, Y) coordinate pair of an random unopened square from the
     * current board
     *
     * @param board The current board state
     * @return An (X, Y) coordinate pair
     */
    public Pair<Integer> findUnopenedSquare(Board board) {
        Boolean unOpenedSquare = false;

        LinkedHashSet<Square> opened = new LinkedHashSet(board.getOpenSquares());
        int x;
        int y;

        Pair<Integer> pair = new Pair<>(0, 0);

        while (!unOpenedSquare) {
            x = rng.nextInt(board.width);
            y = rng.nextInt(board.height);
            if (!opened.contains(board.board[x][y]) && !isMarked(board.getSquareAt(x, y))) {
                unOpenedSquare = true;
                pair = new Pair<Integer>(x, y);
            }
        }
        return pair;
    }

    private String squareToString(Square square) {
        if (square == null) {
            return "null";
        }
        return "(" + square.getX() + "," + square.getY() + ")";
    }

    /**
     * Used only for testing/debugging. Converts Square set to String
     *
     * @param s Square set variable
     * @return Square set as String
     */
    private String squareSetToString(LinkedHashSet<Square> s) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for (Square square : s) {
            if (sb.length() != 1) {
                sb.append(", ");
            }
            sb.append(squareToString(square));

        }
        sb.append("}");
        return sb.toString();
    }
    
    public boolean prevMoveWasRandom() {
        return this.prevMoveWasRandom;
    }

    /**
     * Returns an open move to a specified square.
     *
     * @param square square for which the open move is made
     * @return open move to the specified square
     */
    private Move openMove(Square square) {
        opened.add(square);
        return new Move(MoveType.OPEN, square.getX(), square.getY());
    }
}