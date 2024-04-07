package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;

@Entity
@Table(name = "ACHIEVEMENTSTATUS")
public class AchievementStatus {
    @Id
    private long UserId;

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long UserId) {
        this.UserId = UserId;
    }

    @Column
    private boolean first;
    @Column
    private int firstProgress;

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
