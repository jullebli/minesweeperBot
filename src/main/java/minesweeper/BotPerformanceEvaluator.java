package minesweeper;

import java.util.Random;
import minesweeper.bot.Bot;
import minesweeper.bot.FirstBot;
import minesweeper.bot.SecondBot;
import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.GameResult;
import minesweeper.model.Move;

public class BotPerformanceEvaluator {

    public static void main(String[] args) {
        BotPerformanceEvaluator evaluator = new BotPerformanceEvaluator();

        evaluator.startEvaluation();
    }

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

        //FirstBot: Beginner, Intermediate, Expert
        playManyGames(10, 10, 10, "FirstBot", 100);

        playManyGames(16, 16, 40, "FirstBot", 100);

        playManyGames(16, 30, 99, "FirstBot", 100);

        //SecondBot: Beginner, Intermediate, Expert
       /* System.out.println(playManyGames(10, 10, 10, 2, 100));

        System.out.println(playManyGames(16, 16, 40, 2, 100));

        System.out.println(playManyGames(16, 30, 99, 2, 100)); */
    }

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

        System.out.println("Bot: " + botName + ", success rate: " + 
                ((double) timesWon / times * 100.0) + "%, average duration: " + 
                (timeSum / times) + ", width: " + width + ", height: " +
                height + ", mines: " + mines);
    }
    
    private Bot getBotByName(String botName) {
            if (botName.equals("FirstBot")) {
                return new FirstBot();
            } else if (botName.equals("SecondBot")) {
                return new SecondBot();
            } else {
                throw new RuntimeException("Unknown botNumber");
            }
    }
}
