package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

@Entity
@Table(name = "ACHIEVEMENTSTATUS")
public class AchievementStatus {
    @Id
    private long userId;
    @Column
    private boolean baron1 = false;
    private boolean baron2 = false;
    private boolean baron3 = false;
    @Column
    private int maxMoneyInGame = 0;
    @Column
    private boolean noMoney = false;
    @Column
    private int winLeastAmountMoney = 1000;
    @Column
    private boolean noUltimate;
    @Column
    private boolean endurance1 = false;
    @Column
    private boolean endurance2 = false;
    @Column
    private boolean endurance3 = false;
    @Column
    private boolean gamer = false;
    @Column
    private int totalGamesWon = 0;
    @Column
    private int winStreak = 0;
    @Column
    private boolean doingYourBest = false;
    @Column
    private int loseStreak = 0;
    @Column
    private boolean noWinner = false;
    @Column
    private boolean backStabber = false;

    public AchievementStatus(long id){
        this.userId = id;
    }

    public AchievementStatus(){
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isBaron1() {
        return baron1;
    }

    public void setBaron1(boolean baron1) {
        this.baron1 = baron1;
    }

    public boolean isBaron2() {
        return baron2;
    }

    public void setBaron2(boolean baron2) {
        this.baron2 = baron2;
    }

    public boolean isBaron3() {
        return baron3;
    }

    public void setBaron3(boolean baron3) {
        this.baron3 = baron3;
    }

    public boolean isEndurance1() {
        return endurance1;
    }
    public void setEndurance1(boolean endurance1) {
        this.endurance1 = endurance1;
    }
    public boolean isEndurance2() {
        return endurance2;
    }
    public void setEndurance2(boolean endurance2) {
        this.endurance2 = endurance2;
    }
    public boolean isEndurance3() {
        return endurance3;
    }
    public void setEndurance3(boolean endurance3) {
        this.endurance3 = endurance3;
    }

    public int getMaxMoneyInGame() {
        return maxMoneyInGame;
    }

    public void setMaxMoneyInGame(int maxMoneyInGame) {
        this.maxMoneyInGame = maxMoneyInGame;
    }

    public int getWinLeastAmountMoney() {
        return winLeastAmountMoney;
    }

    public void setWinLeastAmountMoney(int winLeastAmountMoney) {
        this.winLeastAmountMoney = winLeastAmountMoney;
    }

    public boolean isNoMoney() {
        return noMoney;
    }

    public void setNoMoney(boolean noMoney) {
        this.noMoney = noMoney;
    }

    public boolean isNoUltimate() {
        return noUltimate;
    }

    public void setNoUltimate(boolean noUltimate) {
        this.noUltimate = noUltimate;
    }

    public boolean isGamer() {
        return gamer;
    }

    public void setGamer(boolean gamer) {
        this.gamer = gamer;
    }

    public int getTotalGamesWon() {
        return totalGamesWon;
    }

    public void setTotalGamesWon(int totalGamesWon) {
        this.totalGamesWon = totalGamesWon;
    }

    public void incTotalGamesWon(){
        this.totalGamesWon += 1;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }

    public void incWinStreak(){
        this.winStreak += 1;
    }

    public boolean isDoingYourBest() {
        return doingYourBest;
    }

    public void setDoingYourBest(boolean doingYourBest) {
        this.doingYourBest = doingYourBest;
    }

    public int getLoseStreak() {
        return loseStreak;
    }

    public void setLoseStreak(int loseStreak) {
        this.loseStreak = loseStreak;
    }

    public void incLoseStreak(){
        this.loseStreak += 1;
    }

    public boolean isNoWinner() {
        return noWinner;
    }
    public void setNoWinner(boolean noWinner) {
        this.noWinner = noWinner;
    }

    public boolean isBackStabber() {
        return backStabber;
    }

    public void setBackStabber(boolean backStabber) {
        this.backStabber = backStabber;
    }
}
