package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

@Entity
@Table(name = "ACHIEVEMENTSTATUS")
public class AchievementStatus {
    @Id
    private long userId;
    @Column
    private boolean first;
    @Column
    private int firstProgress;

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

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getFirstProgress() {
        return firstProgress;
    }

    public void setFirstProgress(int firstProgress) {
        this.firstProgress = firstProgress;
    }
}
