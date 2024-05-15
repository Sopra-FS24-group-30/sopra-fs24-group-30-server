package ch.uzh.ifi.hase.soprafs24.logic.Game;

public class AchievementProgress {

    private Long userId;
    private int maxAmountCash;
    private int cashWhenWinning;
    private boolean winner;
    private boolean ultimateUsed;

    public AchievementProgress(Long userId){
        this.userId = userId;
    }


    public Long getUserId() {
        return userId;
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
}
