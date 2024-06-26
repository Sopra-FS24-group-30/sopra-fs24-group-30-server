package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

@Entity
@Table (name="User")
public class User implements Serializable {

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
    @Column(nullable = true)
    private LocalDate birthday;
    @Column int amountGamesCompleted;
    @Column(nullable = false)
    private UserStatus status;
    @Column
    private int amountWins;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id",referencedColumnName = "UserId")
    private AchievementStatus achievement; //NOSONAR

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

    public void setStatus(UserStatus status){this.status = status; }

    public UserStatus getStatus(){return status; }

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

    public void setBirthday(LocalDate birthday){
        this.birthday = birthday;
    }
    public LocalDate getBirthday(){return birthday; }
}
