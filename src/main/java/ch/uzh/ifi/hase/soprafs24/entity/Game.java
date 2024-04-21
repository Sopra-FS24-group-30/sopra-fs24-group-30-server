package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;

/**
 * Internal Game Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unique across the database -> composes
 * the primary key
 */

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "game_player_ids", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "player_id")
    private List<String> players;

    @Column(nullable = false)
    private GameStatus status;

    @Column(nullable = false)
    private Integer roundNum=1;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "game")
    @JsonManagedReference
    private GameBoard gameBoard;

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoundNum(Integer roundNum) {
        this.roundNum = roundNum;
    }
    public Integer getRoundNum() {
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

    public void setPlayers(List<String> playerList){
        this.players = playerList;
    }

    public void addPlayer(String Id){
        this.players.add(Id);
    }

    public List<String> getPlayers() {
        return players;
    }

    public void removePlayer(String Id){
        this.players.remove(Id);
    }
}
