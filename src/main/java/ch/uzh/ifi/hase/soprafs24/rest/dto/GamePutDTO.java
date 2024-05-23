package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;

import java.util.List;

public class GamePutDTO {
    private GameStatus status;
    private List<Player> active_players;//NOSONAR

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public List<Player> getactive_Players() {//NOSONAR
        return active_players;
    }

    public void setactive_players(List<Player> active_players) {//NOSONAR
        this.active_players = active_players;
    }


}
