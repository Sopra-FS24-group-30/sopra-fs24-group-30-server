package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Internal Game Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long gameId;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    private GameStatus status;

    @Column(nullable = false)
    private Integer roundNum;

    public Long getId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setGameStatus(GameStatus status) {
        this.status = status;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void startGame() {
        this.roundNum = 1;
    }

    public void nextRound() {
        this.roundNum++;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }
}
