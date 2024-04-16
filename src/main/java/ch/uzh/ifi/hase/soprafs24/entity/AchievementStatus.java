package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

@Entity
@Table(name = "ACHIEVEMENTSTATUS")
public class AchievementStatus {
    @Id
    private long UserId;
    @Column
    private boolean first;
    @Column
    private int firstProgress;

    public AchievementStatus(long id){
        this.UserId = id;
    }

    public AchievementStatus(){
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long UserId) {
        this.UserId = UserId;
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
