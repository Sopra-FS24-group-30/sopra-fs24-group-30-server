package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Effects.Getem;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.logic.Game.WinConditionUltimate;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;

import java.util.ArrayList;

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

    private transient List<Player> active_players = new ArrayList<>(); //NOSONAR

    @Column(nullable = false)
    private GameStatus status;

    @Column(nullable = false)
    private Integer roundNum=1;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "game")
    @JsonManagedReference
    private GameBoard gameBoard;

    @ElementCollection
    private List<String> listOfAllCondition = WinConditionUltimate.getAllWinConditions();
    @ElementCollection
    private List<String> listOfAllUltis = new ArrayList<>(Getem.getUltimates().keySet());

    public List<Player> getactive_Players() {//NOSONAR
        return active_players;
    }

    public void setactive_players(List<Player> active_players) { //NOSONAR
        this.active_players = active_players;
    }

    public void addNEWPlayer(Player player) {
        this.active_players.add(player);
    }

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

    public void addPlayer(String id){
        this.players.add(id);
    }

    public List<String> getPlayers() {
        return players;
    }

    public void removePlayer(String id){
        this.players.remove(id);
    }

    public List<String> getListOfAllCondition() {
        return listOfAllCondition;
    }

    public List<String> getListOfAllUltis() {
        return listOfAllUltis;
    }
}