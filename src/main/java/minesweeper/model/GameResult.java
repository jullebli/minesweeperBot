
package minesweeper.model;

public class GameResult {
    private boolean gameWon;
    private double playTime;
    
    public GameResult(boolean gameWon, double playTime) {
        this.gameWon = gameWon;
        this.playTime = playTime;
    }
    
    public boolean getGameWon() {
        return gameWon;
    }
    
    public double getPlayTime() {
        return playTime;
    }
}
