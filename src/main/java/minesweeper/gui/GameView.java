package minesweeper.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.control.Slider;
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
import minesweeper.bot.TestBot;
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
    private Label animationSpeedLabel = new Label("Bot game animation speed");
    private Slider animationSlider;
    private Button[][] buttonGrid;
    private Button botButton;
    private int buttonSize;
    public final GameStats stats = new GameStats();
    public final long[] currentNanotime = new long[1];

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
        Button botGame;
        this.vbox = vbox;
        sizeX = x;
        int sizeY = y;
        this.buttonGrid = new Button[x][y];
       
        if (x < 11) {
            buttonSize = 45;
        } else if (x < 17) {
            buttonSize = 40;
        } else if (x < 31) {
            buttonSize = 35;
        } else {
            buttonSize = 30;
        }

        this.bot = BotSelect.getBot();

        botButton = new Button("Help (bot)");
        botButton.setOnMouseClicked(e -> {
            this.clearAllHighlights();
            Move move = this.bot.makeMove(board);
            board.makeMove(move);
            stats.update(move);
            if (!board.gameEnd) {
                this.updateGameGP(move.x, move.y);
            } else {
                this.updateGameGP(move.x, move.y);
                this.gameOver();
            }
        });

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

        long[] previousNanoTime = new long[1];
        previousNanoTime[0] = System.nanoTime();

        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                // Time that has passed since last update
              
                long deltaTime = TimeUnit.MILLISECONDS.convert(currentNanoTime - previousNanoTime[0],
                        TimeUnit.NANOSECONDS);

                previousNanoTime[0] = currentNanoTime;

                time += deltaTime;
                timerLabel.setText("Time: " + TimeUnit.SECONDS.convert(time, TimeUnit.MILLISECONDS));
                

                // Kills the timer update routine if the game has ended
                if (board.gameEnd || board.gameWon) {
                    this.stop();
                }

            }
        };

        timer.start();
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
        button.setOnMouseReleased((e) -> {
            boolean nonEndingMove = true;
            switch (e.getButton()) {
                case PRIMARY:
                    if (e.isSecondaryButtonDown() && board.getSquareAt(x, y).isOpened()) {
                        Move chordedOpen = new Move(MoveType.CHORD, x, y);
                        nonEndingMove = this.board.makeMove(chordedOpen);
                        stats.update(chordedOpen);
                        break;
                    }
                    Move open = new Move(MoveType.OPEN, x, y);
                    nonEndingMove = this.board.makeMove(open);
                    stats.update(open);
                    break;
                case SECONDARY:
                    if (e.isPrimaryButtonDown() && board.getSquareAt(x, y).isOpened()) {
                        Move chordedOpen = new Move(MoveType.CHORD, x, y);
                        nonEndingMove = this.board.makeMove(chordedOpen);
                        stats.update(chordedOpen);
                        break;
                    }
                    if (!this.board.getSquareAt(x, y).isOpened()) {
                        Move flag = new Move(MoveType.FLAG, x, y);
                        this.board.makeMove(flag);
                        stats.update(flag);
                    }
                    break;
                default:
                    /* No such button, but don't */
                    break;
            }
            updateGameGP(x, y);
            this.clearAllHighlights();
            if (!nonEndingMove | this.board.gameEnd | this.board.gameWon) {
                gameOver();
            }
        });
        return button;
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
            this.endLabel.setText("You lost.");
            this.endLabel.getStyleClass().add("label-failure");
            this.timerLabel.getStyleClass().add("label-failure");
        }
        this.disableAllButtons();
    }

    /**
     * Updates the view with the current boardstate.
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
            }
        } else {
            if (board.board[x][y].getFlagged()) {
                styleToAdd.add("flagged-button");
            }
            if (!board.board[x][y].getFlagged()) {
                updatedButton.getStyleClass().remove("flagged-button");
            }
        }

        styleToAdd.stream().dropWhile(style -> updatedButton.getStyleClass().contains(style))
                .forEach(newStyle -> updatedButton.getStyleClass().add(newStyle));

        this.endLabel.setText("Mines: " + this.board.getUnflaggedMines());
    }

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
     */
    private void botGameLoop() {
        // Called as if game is over to disable human input
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
                if (board.gameEnd || board.gameWon) {
                    this.stop();
                    gameOver();
                }

            }
        };
        // This encapsulates the bot as a thread, bot gets its own board
        // (deep copy of the guis board) that it uses to make its moves

        BotExecutor botThread = new BotExecutor(moveQueue, bot, botBoard);

        // Starts the gui updater and the bot thread
        timer.start();
        stats.startTime = System.nanoTime();
        botThread.start();
    }

    private String setOpenedButtonColor(Button button, int mines) {
        String labelStyle = "custom-label-";
        labelStyle = labelStyle.concat("" + mines);
        return labelStyle;
    }

    // Used by the gui updater timer to updat the board of the gui
    public void updater(LinkedBlockingQueue<Move> moveQueue, Board board) {
        // Takes a move that has bot has made
        Move move = moveQueue.poll();

        if (move == null) {
            return;
        }

        this.clearAllHighlights();
        // Makes move to the gui board and updates the gui buttons
        board.makeMove(move);
        stats.update(move);
        buttonGrid[move.x][move.y].getStyleClass().add("black-highlight");
        updateGameGP(move.x, move.y);
    }

    private void initializeSlider() {
        
        this.animationSlider = new Slider(100, 2000, StorageSingleton.getInstance().animationSpeed);
        this.animationSlider.setMajorTickUnit(200f);
        this.animationSlider.setBlockIncrement(10f);
        this.animationSlider.setMaxWidth(sizeX * buttonSize);
        this.animationSlider.getStyleClass().add("slider");
    }
}
