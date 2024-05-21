package ch.uzh.ifi.hase.soprafs24.logic.Game;
import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController.GameTimer;

public class AchievementProgress {

    private Long userId;
    private int maxAmountCash;
    private int cashWhenWinning;
    private boolean winner;
    private boolean ultimateUsed;
    private GameTimer gameTimer;
    private int WinnerAmount;

    public AchievementProgress(Long userId){
        this.userId = userId;
    }

    public AchievementProgress(Long userId, GameTimer gameTimer){

        this.userId = userId;
        this.gameTimer = gameTimer;
        //this.gameTimer = GameWebSocketController.getGameTimers().get(gameId);

    }

    public Long getUserId() {
        return userId;
    }

    public GameTimer getGameTimer() {
        return this.gameTimer;
    }

    public void setGameTimer(GameTimer gameTimer){
        this.gameTimer = gameTimer;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getMaxAmountCash() {
        return maxAmountCash;
    }

    public void setMaxAmountCash(int maxAmountMoney) {
        this.maxAmountCash = maxAmountMoney;
    }

    public int getCashWhenWinning() {
        return cashWhenWinning;
    }

    public void setCashWhenWinning(int cashWhenWinning) {
        this.cashWhenWinning = cashWhenWinning;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public boolean isUltimateUsed() {
        return ultimateUsed;
    }

    public void setUltimateUsed(boolean ultimateUsed) {
        this.ultimateUsed = ultimateUsed;
    }

    public void setWinnerAmount(int amount){
        this.WinnerAmount = amount;
    }

    public int getWinnerAmount(){
        return this.WinnerAmount;
    }
}
