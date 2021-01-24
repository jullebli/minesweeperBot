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

public class FirstBot implements Bot {

    private Random rng = new Random();
    private GameStats gameStats;

    /**
     * Make a single decision based on the given Board state
     *
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove(Board board) {
        ArrayList<Move> possibleMoves = getPossibleMoves(board);
        if (!possibleMoves.isEmpty()) {
            return possibleMoves.get(0);
        }
        // Find the coordinate of an unopened square
        Pair<Integer> pair = findUnopenedSquare(board);
        int x = (int) pair.first;
        int y = (int) pair.second;

        // The TestBot isn't very smart and randomly
        // decides what move should be made using java.util.Random
        System.out.println("Random move: " + x + ", " + y);
        System.out.flush();
        return new Move(MoveType.OPEN, x, y);

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
        ArrayList<Move> moves = getPossibleMovesAlgo1(board);
        if (moves.size() > 0) {
            System.out.println("Algo1 Move: " + moves.get(0).x + ", " + moves.get(0).y);
            System.out.flush();
            return moves;
        }

        moves.addAll(getPossibleMovesAlgo2(board));
        if (moves.size() > 0) {
            System.out.println("Algo2 Move: " + moves.get(0).x + ", " + moves.get(0).y);
            System.out.flush();
            return moves;
        }
        return moves;
    }

    private ArrayList<Move> getPossibleMovesAlgo1(Board board) {
        ArrayList<Move> movesToMake = new ArrayList<>();
        HashSet<Square> opened = board.getOpenSquares();

        for (Square square : opened) {
            int surroundingMines = square.surroundingMines();
            HashSet<Square> surroundingUnopened = new HashSet<>();

            for (int xInc = -1; xInc <= 1; xInc++) {
                for (int yInc = -1; yInc <= 1; yInc++) {
                    if (xInc == 0 && yInc == 0) {
                        continue;
                    }
                    if (board.withinBoard(square.getX() + xInc, square.getY() + yInc)) {
                        Square s = board.getSquareAt(square.getX() + xInc, square.getY() + yInc);
                        if (!s.isOpened()) {
                            surroundingUnopened.add(s);
                        }
                    }
                }
            }
            if (surroundingMines == surroundingUnopened.size()) {
                for (Square unopenedSquare : surroundingUnopened) {
                    // movesToMake.add(new Move(unopenedSquare.getX(), unopenedSquare.getY(), Highlight.RED));
                    if (!unopenedSquare.isFlagged()) {
                        movesToMake.add(new Move(MoveType.FLAG, unopenedSquare.getX(), unopenedSquare.getY()));
                    }
                }
            }
        }

        return movesToMake;
    }

    private ArrayList<Move> getPossibleMovesAlgo2(Board board) {
        ArrayList<Move> movesToMake = new ArrayList<>();
        HashSet<Square> opened = board.getOpenSquares();

        for (Square square : opened) {
            int surroundingMines = square.surroundingMines();
            int surroundingFlags = 0;
            HashSet<Square> surroundingUnopenedUnFlagged = new HashSet<>();

            for (int xInc = -1; xInc <= 1; xInc++) {
                for (int yInc = -1; yInc <= 1; yInc++) {
                    if (xInc == 0 && yInc == 0) {
                        continue;
                    }
                    if (board.withinBoard(square.getX() + xInc, square.getY() + yInc)) {
                        Square s = board.getSquareAt(square.getX() + xInc, square.getY() + yInc);
                        if (!s.isOpened() && !s.isFlagged()) {
                            surroundingUnopenedUnFlagged.add(s);
                        }
                        if (s.isFlagged()) {
                            surroundingFlags++;
                        }
                    }
                }
            }
            if (surroundingMines == surroundingFlags) {
                for (Square unopenedSquare : surroundingUnopenedUnFlagged) {
                    // movesToMake.add(new Move(unopenedSquare.getX(), unopenedSquare.getY(), Highlight.RED));

                    movesToMake.add(new Move(MoveType.OPEN, unopenedSquare.getX(), unopenedSquare.getY()));
                }
            }
        }

        return movesToMake;
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
     * Find the (X, Y) coordinate pair of an unopened square from the current
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
            if (!opened.contains(board.board[x][y]) && !board.getSquareAt(x, y).isFlagged()) {
                unOpenedSquare = true;
                pair = new Pair<Integer>(x, y);
            }
        }

        // This pair should point to an unopened square now
        return pair;
    }
}
