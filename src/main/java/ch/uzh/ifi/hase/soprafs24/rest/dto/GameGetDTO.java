package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;

import java.util.List;

public class GameGetDTO {

    private Long id;

    private GameStatus status;

    private GameBoard gameBoard;

    private List<Player> active_players;

    public List<Player> getActive_players() {
        return active_players;
    }

    public void setActive_players(List<Player> active_players) {
        this.active_players = active_players;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
}