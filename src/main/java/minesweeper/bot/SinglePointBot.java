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

/**
 * A bot that is similar to NaiveSinglePointBot, but this bot's code is not
 * based on pseudocode by others.
 *
 */
public class SinglePointBot implements Bot {

    private Random rng = new Random();
    private GameStats gameStats;
    private int movesMade = 0;
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
        if (movesMade++ == 0) {
            //First move
            return new Move(MoveType.OPEN,
                    board.width / 2,
                    board.height / 2);
        }

        MyList<Move> possibleMoves = getPossibleMovesAsMyList(board, false);
        if (!possibleMoves.isEmpty()) {
            return possibleMoves.get(0);
        }

        //open a random square if there are no possible moves
        Pair<Integer> pair = findUnopenedUnflaggedRandomSquare(board);
        int x = (int) pair.first;
        int y = (int) pair.second;

        //System.out.println("Random move: " + x + ", " + y);
        //System.out.flush();
        prevMoveWasRandom = true;
        return new Move(MoveType.OPEN, x, y);

    }

    /**
     * Return multiple possible moves to make based on the current board state.
     *
     * @param board The current board state
     * @return List of moves for current board
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
     * Return multiple possible moves to make based on the current board state.
     *
     * @param board The current board state
     * @param returnAllMoves true if all possible moves are wanted, false if possible
     * open moves are not wanted if there are possible flag moves
     * @return List of moves for current board
     */
    private MyList<Move> getPossibleMovesAsMyList(Board board, boolean returnAllMoves) {
        MyList<Move> moves = getPossibleMovesUsingAMN(board);
        if (moves.size() > 0 && !returnAllMoves) {
            // System.out.println("Algo1 Move: " + moves.get(0).x + ", " + moves.get(0).y);
            // System.out.flush();
            return moves;
        }

        moves.addAll(getPossibleMovesUsingAFN(board));
        if (moves.size() > 0 && !returnAllMoves) {
            //System.out.println("Algo2 Move: " + moves.get(0).x + ", " + moves.get(0).y);
            //System.out.flush();
            return moves;
        }
        return moves;
    }

    /**
     * Returns a list of flag moves for squares that have mines using AMN (All
     * Mine/Mark Neighbours) principle. The case of AMN: if a square has the
     * same amount of unopened neighbouring squares as its number of adjacent 
     * mines then all unopened neighbouring squares contain mines.
     *
     * @see "Becerra, David J. 2015. Algorithmic Approaches to
     * PlayingMinesweeper. Bachelor's thesis, Harvard College. Link:
     * <a href="http://nrs.harvard.edu/urn-3:HUL.InstRepos:14398552">"
     *
     * @param board The current board state
     * @return a list of flag moves
     */
    private MyList<Move> getPossibleMovesUsingAMN(Board board) {
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

    /**
     * Returns a list of open moves for squares that don't have mines using AFN
     * (All Free Neighbours) principle. The case of AFN: if a square has the
     * same amount of marked/flagged neighbouring squares as its number of
     * adjacent mines then all unmarked/unflagged neighbouring squares do not
     * contain mines.
     *
     * @see "Becerra, David J. 2015. Algorithmic Approaches to
     * PlayingMinesweeper. Bachelor's thesis, Harvard College. Link:
     * <a href="http://nrs.harvard.edu/urn-3:HUL.InstRepos:14398552">"
     * @param board The current board state
     * @return a list of squares that don't have a mine
     */
    private MyList<Move> getPossibleMovesUsingAFN(Board board) {
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
                    movesToMake.add(new Move(MoveType.OPEN,
                            unopenedSquare.getX(), unopenedSquare.getY()));
                }
            }
        }
        return movesToMake;
    }
    
    public boolean prevMoveWasRandom() {
        return this.prevMoveWasRandom;
    }

    /**
     * Used to pass the bot the gameStats object, useful for tracking previous
     * moves.
     */
    @Override
    public void setGameStats(GameStats gameStats) {
        this.gameStats = gameStats;
    }

    /**
     * Find the (X, Y) coordinate pair of an unopened unflagged square from the
     * current board.
     *
     * @param board The current board state
     * @return An (X, Y) coordinate pair
     */
    public Pair<Integer> findUnopenedUnflaggedRandomSquare(Board board) {
        Boolean unOpenedSquare = false;

        MySet<Square> opened = new MySet(board.getOpenSquares());
        int x;
        int y;

        Pair<Integer> pair = new Pair<>(0, 0);

        while (!unOpenedSquare) {
            x = rng.nextInt(board.width);
            y = rng.nextInt(board.height);
            if (!opened.contains(board.board[x][y]) && !board.getSquareAt(x, y).isFlagged()) {
                unOpenedSquare = true;
                pair = new Pair<Integer>(x, y);
            }
        }
        return pair;
    }
}
