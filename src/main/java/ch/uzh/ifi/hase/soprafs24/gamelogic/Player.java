package ch.uzh.ifi.hase.soprafs24.gamelogic;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

public class Player {

    @Id
    @GeneratedValue
    private Long playerId;
    private String playerName;
    private int cash;
    private Long position;

    private List<Card> cards;
    private List<Item> items;
    private Ultimate ultimate;

    private Long userId;
    private Long teammateId;
    private Long gameBoardId;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Ultimate getUltimate() {
        return ultimate;
    }

    public void setUltimate(Ultimate ultimate) {
        this.ultimate = ultimate;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTeammateId() {
        return teammateId;
    }

    public void setTeammateId(Long teammateId) {
        this.teammateId = teammateId;
    }

    public Long getGameBoardId() {
        return gameBoardId;
    }

    public void setGameBoardId(Long gameBoardId) {
        this.gameBoardId = gameBoardId;
    }
}
