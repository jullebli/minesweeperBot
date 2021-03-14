package minesweeper;

import java.util.Random;
import minesweeper.bot.Bot;
import minesweeper.bot.SinglePointBot;
import minesweeper.bot.DoubleSetSinglePointBot;
import minesweeper.bot.NaiveSinglePointBot;
import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.GameResult;
import minesweeper.model.Move;
import java.util.ArrayList;

/**
 * Used for comparing results from using different bots all playing a specified
 * same amount of minesweeper games in three difficulties(Beginner,
 * Intermediate, Expert).
 */
public class BotPerformanceEvaluator {

    public static void main(String[] args) {
        BotPerformanceEvaluator evaluator = new BotPerformanceEvaluator();

        evaluator.startEvaluation();
    }

    /**
     * Play one minesweeper game with a specified bot and with a board with
     * specified size and amount of mines.
     *
     * @param width width of the board
     * @param height height of the board
     * @param mines amount of mines to be placed on the board
     * @param bot bot used to play the game
     * @return GameResult variable containing information about played the game
     */
    public GameResult playOneGame(int width, int height, int mines, Bot bot) {
        Random random = new Random();
        MinefieldGenerator generator = new MinefieldGenerator(random.nextInt());

        Board board = new Board(generator, width, height, mines);

        double start = System.nanoTime() / 1000000000.0;

        while (!board.gameLost && !board.gameWon) {
            Move move = bot.makeMove(board);
            board.makeMove(move);
        }

        double end = System.nanoTime() / 1000000000.0;

        return new GameResult(board.gameWon, end - start);
    }

    private void startEvaluation() {
        ArrayList<String> botNames = new ArrayList<>();
        botNames.add("SinglePointBot");
        botNames.add("DoubleSetSinglePointBot");
        botNames.add("NaiveSinglePointBot");

        for (String botName : botNames) {
            playManyGames(10, 10, 10, botName, 1000); //Beginner
            playManyGames(16, 16, 40, botName, 1000); //Intermediate
            playManyGames(16, 30, 99, botName, 1000); //Expert
        }
    }

    /**
     * Play many minesweeper games with a specified bot and with different
     * boards with specified size and amount of mines.
     *
     * @param width width of the board
     * @param height height of the board
     * @param mines amount of mines to be placed on the board
     * @param botName the name of the bot that will be used to play the games
     * @param times the amount of times minesweeper will be played
     */
    private void playManyGames(int width, int height, int mines, String botName,
            int times) {

        int timesWon = 0;
        double timeSum = 0;

        for (int i = 0; i < times; i++) {
            Bot bot = getBotByName(botName);

            GameResult result = playOneGame(width, height, mines, bot);
            if (result.getGameWon()) {
                timesWon++;
            }
            timeSum += result.getPlayTime();
        }

        System.out.println("Bot: " + botName + ", success rate: "
                + ((double) timesWon / times * 100.0) + "%, average duration: "
                + (timeSum / times) + ", width: " + width + ", height: "
                + height + ", mines: " + mines);
    }

    /**
     * Returns a new bot object specified by a given name. The name of the bot
     * (the bot's class name) must be written correctly or a RunTimeException is
     * thrown.
     *
     * @param botName the name of the bot to be created
     * @return a new bot object specified by the given name
     */
    private Bot getBotByName(String botName) {
        if (botName.equals("SinglePointBot")) {
            return new SinglePointBot();
        } else if (botName.equals("DoubleSetSinglePointBot")) {
            return new DoubleSetSinglePointBot();
        } else if (botName.equals("NaiveSinglePointBot")) {
            return new NaiveSinglePointBot();
        } else {
            throw new RuntimeException("Unknown botNumber");
        }
    }
}
