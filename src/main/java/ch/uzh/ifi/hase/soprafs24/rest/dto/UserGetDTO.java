package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.AchievementStatus;

import java.time.LocalDate;

public class UserGetDTO {

    private long id;
    private String username;
    private String token;
    private LocalDate creationDate;
    private AchievementStatus achievement;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public AchievementStatus getAchievement() {
        return achievement;
    }

    public void setAchievement(AchievementStatus achievement) {
        this.achievement = achievement;
    }
}
