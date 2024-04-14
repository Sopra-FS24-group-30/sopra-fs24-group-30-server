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
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private GameStatus status;

    @Column(nullable = false)
    private Integer roundNum;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "game")
    private GameBoard gameBoard;

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        gameBoard.setGame(this);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }


    public Long getId() {
        return id;
    }

    public void setid(Long id) {
        this.id = id;
    }

    public Integer setRoundNum(Integer roundNum) {
        this.roundNum = roundNum;
    }
    public void getroundNum() {
        return roundNum;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }


    public void startGame() {
        this.roundNum = 1;
    }

    public void nextRound() {
        this.roundNum++;
    }
}
