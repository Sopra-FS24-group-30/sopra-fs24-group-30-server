package ch.uzh.ifi.hase.soprafs24.logic.Game;
import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class AchievementProgress {

    private Long userId;
    private int maxAmountCash;
    private int cashWhenWinning;
    private boolean winner;
    private boolean teamMateWinner;
    private boolean ultimateUsed;
    private int WinnerAmount;
    private long elapsedSeconds;

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

    public boolean isTeamMateWinner() {
        return teamMateWinner;
    }

    public void setTeamMateWinner(boolean teamMateWinner) {
        this.teamMateWinner = teamMateWinner;
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

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void setElapsedSeconds(long elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }
}
