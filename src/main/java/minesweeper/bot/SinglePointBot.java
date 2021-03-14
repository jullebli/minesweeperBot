package minesweeper.bot;

import java.util.Random;
import java.util.ArrayList; //getPossibleMoves needs to return an ArrayList
import minesweeper.util.MyList;
import minesweeper.util.MySet;
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
    private int movesMade = 0;

    /**
     * Make a single decision based on the given Board state
     *
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove(Board board) {
        if (movesMade++ == 0) {
            return new Move(MoveType.OPEN,
                    board.width / 2,
                    board.height / 2);
        }

        MyList<Move> possibleMoves = getPossibleMovesAsMyList(board, false);
        if (!possibleMoves.isEmpty()) {
            return possibleMoves.get(0);
        }
        // Find the coordinate of an unopened square
        Pair<Integer> pair = findUnopenedSquare(board);
        int x = (int) pair.first;
        int y = (int) pair.second;

        //System.out.println("Random move: " + x + ", " + y);
        //System.out.flush();
        return new Move(MoveType.OPEN, x, y);

    }

    /**
     * Return multiple possible moves to make based on current board state.
     *
     * @param board The current board state.
     * @return List of moves for current board.
     */
    @Override
    public ArrayList<Move> getPossibleMoves(Board board) {
        MyList<Move> moves = getPossibleMovesAsMyList(board, true);
        MyList<Move> highlightMoves = new MyList();
        
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            if (move.type == MoveType.OPEN) {
                highlightMoves.add(new Move(move.x, move.y, Highlight.GREEN));
            } else if (move.type == MoveType.FLAG) {
                highlightMoves.add(new Move(move.x, move.y, Highlight.RED));
            }
            
        }
        return highlightMoves.toArrayList();
    }

    /**
     * Return multiple possible moves to make based on current board state.
     *
     * @param board The current board state.
     * @return List of moves for current board.
     */
    private MyList<Move> getPossibleMovesAsMyList(Board board, boolean returnAllMoves) {
        MyList<Move> moves = getPossibleMovesAlgo1(board);
        if (moves.size() > 0 && !returnAllMoves) {
            // System.out.println("Algo1 Move: " + moves.get(0).x + ", " + moves.get(0).y);
            // System.out.flush();
            return moves;
        }

        moves.addAll(getPossibleMovesAlgo2(board));
        if (moves.size() > 0 && !returnAllMoves) {
            //System.out.println("Algo2 Move: " + moves.get(0).x + ", " + moves.get(0).y);
            //System.out.flush();
            return moves;
        }
        return moves;
    }

    //AMN
    /**
     * Find out squares that are mines and those squares are flagged.
     *
     *
     * @param board The current board state
     * @return a list of flag moves
     */
    private MyList<Move> getPossibleMovesAlgo1(Board board) {
        MyList<Move> movesToMake = new MyList<>();
        MySet<Square> opened = new MySet(board.getOpenSquares());

        for (Square square : opened) {
            int surroundingMines = square.surroundingMines();
            if (surroundingMines == 0) {
                continue;
            }
            MySet<Square> surroundingUnopened = new MySet<>();

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

    //AFN
    /**
     * Find out squares that don't have mines. They are safe to open and are
     * unflagged.
     *
     * @param board The current board state.
     * @return a list of squares that don't have a mine.
     */
    private MyList<Move> getPossibleMovesAlgo2(Board board) {
        MyList<Move> movesToMake = new MyList<>();
        MySet<Square> opened = new MySet(board.getOpenSquares());

        for (Square square : opened) {
            int surroundingMines = square.surroundingMines();
            int surroundingFlags = 0;
            MySet<Square> surroundingUnopenedUnFlagged = new MySet<>();

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
        MySet<Square> opened = new MySet(board.getOpenSquares());
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
