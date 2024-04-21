package ch.uzh.ifi.hase.soprafs24.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameJoinRequest {

    @JsonProperty("gameID") // Ensure this matches exactly with the JSON property
    private String gameId;

    @JsonProperty("playerId")
    private String playerId;

    // Getters and setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
