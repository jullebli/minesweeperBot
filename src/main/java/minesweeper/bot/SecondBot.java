package minesweeper.bot;

import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
    private Set<Square> setS = new LinkedHashSet<>();
    private Set<Square> setQ = new LinkedHashSet<>();
    private Set<Square> marked = new HashSet<>();
    private Set<Square> opened = new HashSet<>();
    private ArrayList<Square> flagsToDraw = new ArrayList<>();
    private Square prev; //most recently opened square
    private boolean makeRandomMoves = false;

    /**
     * Make a single decision based on the given Board state
     *
     * @param board The current board state.
     * @return Move to be made onto the board.
     */
    @Override
    public Move makeMove(Board board) {
        if (!flagsToDraw.isEmpty()) {
            Square s = flagsToDraw.get(0);
            flagsToDraw.remove(0);
            return new Move(MoveType.FLAG, s.getX(), s.getY());
        }
        if (makeRandomMoves) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {

            }
            return new Move(MoveType.FLAG, board.width - 1, board.height - 1);
        }
        System.out.println("makeMove called: S = "
                + squareSetToString(setS) + "\n Q = "
                + squareSetToString(setQ) + "\n marked = "
                + squareSetToString(marked) + "\n prev = "
                + squareToString(prev));

        if (firstMove) {
            firstMove = false;
            prev = board.getSquareAt(0, 0);
            System.out.println("First move");
            return openMove(prev);
        }

        if (isAFN(prev, board)) {
            System.out.println("isAFN = true");
            setS.addAll(getUnmarkedNeighbours(prev, board));
        } else {
            System.out.println("isAFN = false, prev added to setQ");
            setQ.add(prev);
        }

        while (true) {
            if (!setS.isEmpty()) {
                prev = setS.iterator().next();
                setS.remove(prev);
                System.out.println("setS is not empty returning "
                        + squareToString(prev));
                return openMove(prev);
            }

            Iterator<Square> it = setQ.iterator();
            while (it.hasNext()) {
                Square q = it.next();
                System.out.println("Checking isAMN(" + squareToString(q) + ")");
                if (isAMN(q, board)) {
                    System.out.println("isAMN = true");
                    for (Square y : getUnmarkedNeighbours(q, board)) {
                        System.out.println("marked.add(" + squareToString(y) + ")");
                        marked.add(y);
                        flagsToDraw.add(y);
                    }
                    it.remove();
                }
            }

            Iterator<Square> it2 = setQ.iterator();
            while (it2.hasNext()) {
                Square q = it2.next();
                System.out.println("Checking isAFN(" + squareToString(q) + ")");
                if (isAFN(q, board)) {
                    System.out.println("isAFN = true");
                    setS.addAll(getUnmarkedNeighbours(q, board));
                    it2.remove();
                }
            }

            if (setS.isEmpty()) {
                //Here should be the third algorithm CSP? Now just random square
                System.out.println("Picking random (S is empty)");
                Pair<Integer> pair = findUnopenedSquare(board);
                int x = (int) pair.first;
                int y = (int) pair.second;
                makeRandomMoves = true;
                return new Move(MoveType.FLAG, board.width - 1, board.height - 1);

                //setS.add(board.getSquareAt(x, y));
            }
        }
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
     * Determine if an opened square fills in the AFN terms. In the case of AFN
     * (All free neighbours) the square has as many flagged neighbours as it's
     * number. So all other neighbours except the flagged ones are safe to open.
     *
     * @param square the square which neighbours are examined.
     * @param board The current board state.
     * @return if square is an AFN case.
     */
    private boolean isAFN(Square square, Board board) {
        int flaggedNeighbours = 0;

        for (int xInc = -1; xInc <= 1; xInc++) {
            for (int yInc = -1; yInc <= 1; yInc++) {
                if (xInc == 0 && yInc == 0) {
                    continue;
                }
                if (board.withinBoard(square.getX() + xInc, square.getY() + yInc)) {
                    Square s = board.getSquareAt(square.getX() + xInc, square.getY() + yInc);
                    if (!s.isOpened() && isMarked(s)) {
                        flaggedNeighbours++;
                    }
                }
            }
        }
        System.out.println("isAFN(" + squareToString(square) + ") = " + flaggedNeighbours);
        return square.surroundingMines() == flaggedNeighbours;
    }

    /**
     * /**
     * Determine if an opened square fills in the AMN terms. In the case of AMN
     * (All mine/marked neighbours) the square has as many unopened neighbours
     * as it's number. So all of it's neighbours are mines.
     *
     * @param square the square which neighbours are examined.
     * @param board the current board state.
     * @return if square is an AMN case.
     */
    private boolean isAMN(Square square, Board board) {
        Set<Square> markedNeighbours = getMarkedNeighbours(square, board);
        System.out.println("AMN:markedNeighbours = " + markedNeighbours.size());
        return square.surroundingMines() - markedNeighbours.size()
                == getUnmarkedNeighbours(square, board).size();
    }

    /**
     * Get the square's all unopened unmarked neighbours as a set.
     *
     * @param square the square which neighbours are examined
     * @param board the current board state
     * @return set containing the square's unopened unmarked neighbours
     */
    private Set<Square> getUnmarkedNeighbours(Square square, Board board) {
        Set<Square> neighbours = new LinkedHashSet<>();

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
     * Get the square's all marked neighbours as a set.
     *
     * @param square the square which neighbours are examined
     * @param board the current board state
     * @return set containing the square's marked neighbours
     */
    private Set<Square> getMarkedNeighbours(Square square, Board board) {
        Set<Square> neighbours = new LinkedHashSet<>();

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

    /**
     * Find out if a square is in the marked set.
     *
     * @param square a Square variable
     * @return if the square is in the marked set.
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

        // board.getOpenSquares allows access to already opened squares
        HashSet<Square> opened = board.getOpenSquares();
        int x;
        int y;

        Pair<Integer> pair = new Pair<>(0, 0);

        // Randomly generate X,Y coordinate pairs that are not opened
        while (!unOpenedSquare) {
            x = rng.nextInt(board.width);
            y = rng.nextInt(board.height);
            if (!opened.contains(board.board[x][y]) && !isMarked(board.getSquareAt(x, y))) {
                unOpenedSquare = true;
                pair = new Pair<Integer>(x, y);
            }
        }

        // This pair should point to an unopened square now
        return pair;
    }

    private String squareToString(Square square) {
        if (square == null) {
            return "null";
        }
        return "(" + square.getX() + "," + square.getY() + ")";
    }

    /**
     * Used only for testing/debugging. Converts Square to String
     *
     * @param s Square variable
     * @return Square as String
     */
    private String squareSetToString(Set<Square> s) {
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

    private Move openMove(Square square) {
        opened.add(square);
        return new Move(MoveType.OPEN, square.getX(), square.getY());
    }

}