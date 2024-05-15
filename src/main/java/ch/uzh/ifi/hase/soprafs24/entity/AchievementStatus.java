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
    private int winLeastAmountMoney;
    @Column
    private boolean noUltimate;

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

    public int getMaxMoneyInGame() {
        return maxMoneyInGame;
    }

    public void setMaxMoneyInGame(int maxMoneyInGame) {
        this.maxMoneyInGame = maxMoneyInGame;
    }
}
