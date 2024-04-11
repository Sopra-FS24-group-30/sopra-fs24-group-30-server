package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private LocalDate creationDate;
    @Column int amountGamesCompleted;
    @Column
    private int amountWins;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id",referencedColumnName = "UserId")
    private AchievementStatus achievement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public int getAmountGamesCompleted() {
        return amountGamesCompleted;
    }

    public void setAmountGamesCompleted(int amountGamesCompleted) {
        this.amountGamesCompleted = amountGamesCompleted;
    }

    public int getAmountWins() {
        return amountWins;
    }

    public void setAmountWins(int amountWins) {
        this.amountWins = amountWins;
    }

    public AchievementStatus getAchievement() {
        return achievement;
    }

    public void setAchievement(AchievementStatus achievement) {
        this.achievement = achievement;
    }
}
