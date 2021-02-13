package minesweeper.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Separator;
import javafx.geometry.Orientation;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.MoveType;
import minesweeper.generator.MinefieldGenerator;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import minesweeper.StorageSingleton;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import minesweeper.bot.Bot;
import minesweeper.bot.BotSelect;
import minesweeper.bot.BotExecutor;
import minesweeper.model.Move;
import minesweeper.model.Square;

public class GameView {
    private GridPane gameGP;
    private Board board;
    private Board botBoard;
    private VBox vbox;
    private int sizeX;
    private Bot bot;
    private Label endLabel = new Label("Mines: ");
    private Label timerLabel = new Label("Time: 0");
    private AnimationTimer timer;
    private Label animationSpeedLabel = new Label("Bot game animation speed");
    private Slider animationSlider;
    private Button[][] buttonGrid;
    private Button botButton;
    private Button botGame;
    private int buttonSize;
    private SimpleBooleanProperty leftClick = new SimpleBooleanProperty();
    private SimpleBooleanProperty rightClick = new SimpleBooleanProperty();
    public final GameStats stats = new GameStats();
    public final long[] currentNanotime = new long[1];
    public final long[] timerPreviousTime = new long[1];

    private long time = 0;

    /**
     * Constructor for a game view of given size and mine count Seed for the
     * minefield is generated from system's time
     */
    public GameView(int x, int y, VBox vbox, int mines) {
        this(x, y, vbox, mines, System.nanoTime() / 2L);
    }

    /**
     * Base constructor for GameView with given size, mine count and seed
     */
    public GameView(int x, int y, VBox vbox, int mines, long seed) {
        MinefieldGenerator generator;
        this.vbox = vbox;
        sizeX = x;
        int sizeY = y;
        this.buttonGrid = new Button[x][y];
       
        if (x < 11 && y < 11) {
            buttonSize = 45;
        } else if (x < 17 && y < 17) {
            buttonSize = 40;
        } else if (x < 31 && y < 31) {
            buttonSize = 30;
        } else if (x < 41 && y < 41) {
            buttonSize = 22;
        } else {
            buttonSize = 18;
        }

        this.bot = BotSelect.getBot();

        botButton = new Button("Help (bot)");
        botButton.setOnMouseClicked(e -> {
            this.clearAllHighlights();
            ArrayList<Move> helperMoves = this.bot.getPossibleMoves(board);
            for (Move move : helperMoves) {
                board.makeMove(move);
                stats.update(move);
          
                if (!board.gameLost || board.gameWon) {
                    botGame.setDisable(true);
                    this.updateGameGP(move.x, move.y);
                } else {
                    if (this.board.gameLost) {
                        buttonGrid[move.x][move.y].getStyleClass().add("red-highlight");
                    }
                    this.updateGameGP(move.x, move.y);
                    this.gameOver();
                }
            }
        });

        this.bot.setGameStats(this.stats);

        animationSpeedLabel.setMinWidth(sizeX * buttonSize);
        animationSpeedLabel.getStyleClass().add("label-subheader");
        initializeSlider();

        VBox animationSpeedVBox = new VBox(animationSpeedLabel, this.animationSlider);
        animationSpeedVBox.setVisible(false);

        botGame = new Button("Bot Game");
        botGame.setOnMouseClicked(e -> {
            animationSpeedVBox.setVisible(true);
            this.botGameLoop();
        });

        Button newGame = new Button();
        for (Node n : vbox.getChildren()) {
            if (n instanceof Button) {
                newGame = (Button) n;
            }
        }

        Button statsButton = new Button("Statistics");
        statsButton.setOnMouseClicked(e -> {
            new StatsView(this.stats);
        });
       
        timerLabel.getStyleClass().add("label-subheader");

        newGame.getStyleClass().add("menu-button");
        botButton.getStyleClass().add("menu-button");
        botGame.getStyleClass().add("menu-button");
        statsButton.getStyleClass().add("menu-button");

        HBox hb = new HBox();
        hb.getChildren().add(newGame);
        hb.getChildren().add(botButton);
        hb.getChildren().add(botGame);
        hb.getChildren().add(statsButton);

        this.vbox.getChildren().add(hb);

        this.vbox.getChildren().add(new HBox(this.endLabel, new Separator(Orientation.VERTICAL), timerLabel));

        gameGP = new GridPane();
        gameGP.setMaxWidth(sizeX * buttonSize);
        gameGP.getStyleClass().add("custom-gridpane");
        vbox.getChildren().add(gameGP);
        this.vbox.getChildren().add(animationSpeedVBox);

        generator = new MinefieldGenerator(seed);

        board = new Board(generator, x, y, mines);
        this.endLabel.setText(this.endLabel.getText() + board.getUnflaggedMines());
        this.endLabel.getStyleClass().add("label-subheader");
        Function<Square, Void> observerFunction = new Function<Square, Void>() {
            @Override
            public Void apply(Square observed) {
                updateGameGP(observed.getX(), observed.getY());
                /*
                 * Somewhat hacky, only because java does not allow instancing Void type, and
                 * Object cannot be parameterized nor does it suffice as a Void. This null is
                 * not and even cannot be addressed anywhere.
                 */
                return null;
            }
        };
        board.setChangeObserver(observerFunction);
        botBoard = new Board(generator, x, y, mines);
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Button button = buildButton(new Button(), buttonSize, i, j);
                gameGP.add(button, i, j);
                buttonGrid[i][j] = button;
            }
        }

        // NOTE: This variable is an array for interior mutability, variables passed into
        // AnimationTimers need to be final (for concurrency) but we need to be able to mutate
        // this one

        timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                // Time that has passed since last update
              
                long deltaTime = TimeUnit.MILLISECONDS.convert(currentNanoTime - timerPreviousTime[0],
                        TimeUnit.NANOSECONDS);

                timerPreviousTime[0] = currentNanoTime;

                time += deltaTime;
                timerLabel.setText("Time: " + TimeUnit.SECONDS.convert(time, TimeUnit.MILLISECONDS));
                

                // Kills the timer update routine if the game has ended
                if (board.gameLost || board.gameWon) {
                    this.stop();
                }

            }
        };
    }

    /**
     * Returns a VBox with the view including a GridPane with the gamestate
     */
    public VBox getView() {
        return this.vbox;
    }

    /**
     * Builds a new button with the required functionality for flagging and opening
     * squares
     */
    public Button buildButton(Button button, int size, int x, int y) {
        button.setMinWidth(size);
        button.setMaxWidth(size);
        button.setMinHeight(size);
        button.setMaxHeight(size);
        button.getStyleClass().add("unopened-button");
        // Some Window managers and/or distros seem to shortcut right + left as middle mouse. 
        button.setOnMousePressed((e) -> {
            if (e.getButton() == MouseButton.PRIMARY || e.getButton() == MouseButton.MIDDLE) { 
                this.leftClick.set(true);
            }
            if (e.getButton() == MouseButton.SECONDARY || e.getButton() == MouseButton.MIDDLE) {
                this.rightClick.set(true);
            }
            this.buttonUpdater(x, y);

            timerPreviousTime[0] = System.nanoTime();
            timer.start();
        });
        button.setOnMouseReleased((e) -> {
            if (e.getButton() == MouseButton.PRIMARY || e.getButton() == MouseButton.MIDDLE) { 
                this.leftClick.set(false);
            }
            if (e.getButton() == MouseButton.SECONDARY || e.getButton() == MouseButton.MIDDLE) {
                this.rightClick.set(false);
            }
            botGame.setDisable(true);

            timerPreviousTime[0] = System.nanoTime();
            timer.start();
        });
        return button;
    }
    /**
     * Helper method for button clicking
     */
    private void buttonUpdater(int x, int y) {
        boolean nonEndingMove = true;
        if (this.leftClick.get() && this.rightClick.get()) {
            Move chordedOpen = new Move(MoveType.CHORD, x, y);
            nonEndingMove = this.board.makeMove(chordedOpen);
            stats.update(chordedOpen);
        } else if (this.leftClick.get()) {
            Move open = new Move(MoveType.OPEN, x, y);
            nonEndingMove = this.board.makeMove(open);
            stats.update(open);
        } else if (this.rightClick.get()) {
            if (!this.board.getSquareAt(x, y).isOpened()) {
                Move flag = new Move(MoveType.FLAG, x, y);
                this.board.makeMove(flag);
                stats.update(flag);
            }
        }
        updateGameGP(x, y);
        this.clearAllHighlights();
        if (!nonEndingMove | this.board.gameLost | this.board.gameWon) {
            if (this.board.gameLost) {
                buttonGrid[x][y].getStyleClass().add("red-highlight");
            }
            gameOver();
        }
    }

    /**
     * Updates the view to show that the game has been lost.
     */
    public void gameOver() {
        animationSpeedLabel.setVisible(false);
        animationSlider.setVisible(false);
        this.endLabel.setMinWidth(sizeX * buttonSize / 2);
        this.timerLabel.setMinWidth(sizeX * buttonSize / 2);
        if (this.board.gameWon) {
            this.endLabel.setText("You won!");
            this.endLabel.getStyleClass().add("label-success");
            this.timerLabel.getStyleClass().add("label-success");
        } else {
            this.board.openAllMines();
            this.endLabel.setText("You lost.");
            this.endLabel.getStyleClass().add("label-failure");
            this.timerLabel.getStyleClass().add("label-failure");
            
        }
        this.disableAllButtons();
    }

    /**
     * Update the given X,Y coordinate of the game board
     * @param x The X coordinate
     * @param y the Y coordinate
     */
    public void updateGameGP(int x, int y) {

        gameGP.setMaxWidth(sizeX * buttonSize);
        // gameGP.getStyleClass().add("custom-gridpane");
        Button updatedButton = this.buttonGrid[x][y];
        // Updates the button in the current location with the correct
        // visual representation of the Square.
        switch (board.board[x][y].highlight) {
            case RED:
                updatedButton.getStyleClass().add("red-highlight");
                break;
            case GREEN:
                updatedButton.getStyleClass().add("green-highlight");
                break;
            case BLACK:
                updatedButton.getStyleClass().add("black-highlight");
                break;
            default:
                break;
        }

        ArrayList<String> styleToAdd = new ArrayList<>();
        if (board.board[x][y].isOpened()) {
            updatedButton.getStyleClass().remove("unopened-button");
            styleToAdd.add("opened-button");

            if (board.board[x][y].isMine()) {
                styleToAdd.add("mine");
            } else if (board.board[x][y].surroundingMines() != 0) {
                updatedButton.setText("" + board.board[x][y].surroundingMines());
                styleToAdd.add(setOpenedButtonColor(updatedButton, board.board[x][y].surroundingMines()));
                if (buttonSize < 20) {
                    styleToAdd.add("custom-label-tiny");
                } else if (buttonSize < 25) {
                    styleToAdd.add("custom-label-small");
                } 
            }
        } else {
            if (board.board[x][y].isFlagged()) {
                styleToAdd.add("flagged-button");
            }
            if (!board.board[x][y].isFlagged()) {
                updatedButton.getStyleClass().remove("flagged-button");
            }
        }

        styleToAdd.stream().dropWhile(style -> updatedButton.getStyleClass().contains(style))
                .forEach(newStyle -> updatedButton.getStyleClass().add(newStyle));

        this.endLabel.setText("Mines: " + this.board.getUnflaggedMines());
    }

    /**
     * Iterate through the grid of buttons and clear their highlight styles
     */
    public void clearAllHighlights() {
        this.board.clearHighlights();
        for (Button[] buttonRow : this.buttonGrid) {
            for (Button button : buttonRow) {
                button.getStyleClass().remove("red-highlight");
                button.getStyleClass().remove("green-highlight");
                button.getStyleClass().remove("black-highlight");
            }
        }
    }

    /**
     * Disables all buttons currently in game
     */
    public void disableAllButtons() {
        this.botButton.setDisable(true);
        for (Button[] buttonRow : this.buttonGrid) {
            for (Button button : buttonRow) {
                button.setDisable(true);
                button.getStyleClass().add("no-click");
            }
        }
    }

    /**
     * This method is called when user presses the bot game button.
     * It creates BotExecutor thread and connects to it via a Queue,
     * and initializes a new AnimationTimer to update the GUI based
     * on the bot moves.
     */
    private void botGameLoop() {
        this.botButton.setDisable(true);
        this.disableAllButtons();
        LinkedBlockingQueue<Move> moveQueue = new LinkedBlockingQueue<>();
        currentNanotime[0] = System.nanoTime();
        // This timer updates the gui board with the moves that bot makes
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                // Time that has passed since last update
                StorageSingleton.getInstance().animationSpeed = animationSlider.getValue();
                long deltaTime = TimeUnit.MILLISECONDS.convert(currentNanoTime - currentNanotime[0],
                        TimeUnit.NANOSECONDS);
                // Updates the board only if certain time has passed
                if (deltaTime >= 2200 - animationSlider.getValue()) {
                    updater(moveQueue, board);
                    // Set the time since last update to current time
                    currentNanotime[0] = System.nanoTime();
                }
                // Kills the timer update routine if the game has ended
                if (board.gameLost || board.gameWon) {
                    this.stop();
                    gameOver();
                }

            }
        };
        // This encapsulates the bot as a thread, bot gets its own board
        // (board that is initializes with the same seed) that it uses to make
        // its moves
        BotExecutor botThread = new BotExecutor(moveQueue, bot, botBoard);

        // Starts the gui updater and the bot thread
        timerPreviousTime[0] = System.nanoTime();
        timer.start();
        stats.startTime = System.nanoTime();
        botThread.start();
    }

    /**
     * Determines the Button style class to be used based on
     * the number of adjacent mines.
     * @param button The Button to be updated
     * @param mines The number of mines in
     * @return The CSS style class for this button
     */
    private String setOpenedButtonColor(Button button, int mines) {
        String labelStyle = "custom-label-";
        labelStyle = labelStyle.concat("" + mines);
        return labelStyle;
    }

    // Used by the gui updater timer to updat the board of the gui
    /**
     * Updater function for the GUI when running a bot game
     * Called by the AnimationTimer in botGameLoop()
     * @param moveQueue The queue to which the bot will place its moves
     * @param board The current board for the GUI
     */
    public void updater(LinkedBlockingQueue<Move> moveQueue, Board board) {
        // Takes a move that has bot has made
        Move move = moveQueue.poll();

        if (move == null) {
            return;
        }

        this.clearAllHighlights();
        this.timer.start();
        // Makes move to the gui board and updates the gui buttons
        board.makeMove(move);
        stats.update(move);
        buttonGrid[move.x][move.y].getStyleClass().add("black-highlight");
        updateGameGP(move.x, move.y);
        if (this.board.gameLost) {
            buttonGrid[move.x][move.y].getStyleClass().add("red-highlight");
        }
    }

    /**
     * Initializes the Animation Speed slider for bot games from a StorageSingleton
     * This way the slider speed will persist between multiple games
     */
    private void initializeSlider() {
        this.animationSlider = new Slider(100, 2000, StorageSingleton.getInstance().animationSpeed);
        this.animationSlider.setMajorTickUnit(200f);
        this.animationSlider.setBlockIncrement(10f);
        this.animationSlider.setMaxWidth(sizeX * buttonSize);
        this.animationSlider.getStyleClass().add("slider");
    }
}
