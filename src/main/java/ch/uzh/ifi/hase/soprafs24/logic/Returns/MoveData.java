package ch.uzh.ifi.hase.soprafs24.logic.Returns;

import ch.uzh.ifi.hase.soprafs24.service.GameManagementService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.cfg.NotYetImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoveData {

    @JsonProperty("1")
    private PlayerMove player1 = new PlayerMove();
    @JsonProperty("2")
    private PlayerMove player2 = new PlayerMove();
    @JsonProperty("3")
    private PlayerMove player3 = new PlayerMove();
    @JsonProperty("4")
    private PlayerMove player4 = new PlayerMove();

    private String movetype;
    private static String response = "can only accept values from 1 to 4 you supplied: ";

    public MoveData(ArrayList<Long> playerPos1, ArrayList<Long> playerPos2, ArrayList<Long> playerPos3, ArrayList<Long> playerPos4){
        this.player1.setSpaces(playerPos1);
        this.player2.setSpaces(playerPos2);
        this.player3.setSpaces(playerPos3);
        this.player4.setSpaces(playerPos4);
    }

    public MoveData(String movetype){
        setMovetype(movetype);
    }

    //TODO add change method

    public String getMovetype() {
        return movetype;
    }

    public void setMovetype(String movetype) {
        this.movetype = movetype;
    }


    public void setPlayerSpaceMovesColour(int playerId, ArrayList<Long> playerPosi, int moves, String spaceColour){
        switch (playerId){ //NOSONAR
            case 1:
                player1.setSpaces(playerPosi);
                player1.setMoves(moves);
                player1.setSpaceColour(spaceColour);
                break;
            case 2:
                player2.setSpaces(playerPosi);
                player2.setMoves(moves);
                player2.setSpaceColour(spaceColour);
                break;
            case 3:
                player3.setSpaces(playerPosi);
                player3.setMoves(moves);
                player3.setSpaceColour(spaceColour);
                break;
            case 4:
                player4.setSpaces(playerPosi);
                player4.setMoves(moves);
                player4.setSpaceColour(spaceColour);
                break;
        }
    }

    public Map<String, Object> getPlayerMoveMap(int playerId) {
        Map<String, Object> playerDataMap = new HashMap<>();
        playerDataMap.put("movementType", getMovetype());
        switch (playerId) {
            case 1:
                playerDataMap.put(Integer.toString(playerId), player1);
                break;
            case 2:
                playerDataMap.put(Integer.toString(playerId), player2);
                break;
            case 3:
                playerDataMap.put(Integer.toString(playerId), player3);
                break;
            case 4:
                playerDataMap.put(Integer.toString(playerId), player4);
                break;
            default:
                throw new IllegalArgumentException(response + playerId);
        }
        return playerDataMap;
    }
    public Map<String, Object> getPlayerMoveMap(int playerId1, int playerId2) {
        Map<String, Object> playerDataMap = new HashMap<>();
        playerDataMap.put("movementType", getMovetype());
        switch (playerId1) {
            case 1:
                playerDataMap.put(Integer.toString(playerId1), player1);
                break;
            case 2:
                playerDataMap.put(Integer.toString(playerId1), player2);
                break;
            case 3:
                playerDataMap.put(Integer.toString(playerId1), player3);
                break;
            case 4:
                playerDataMap.put(Integer.toString(playerId1), player4);
                break;
            default:
                throw new IllegalArgumentException(response + playerId2);
        }

        switch (playerId2) {
            case 1:
                playerDataMap.put(Integer.toString(playerId2), player1);
                break;
            case 2:
                playerDataMap.put(Integer.toString(playerId2), player2);
                break;
            case 3:
                playerDataMap.put(Integer.toString(playerId2), player3);
                break;
            case 4:
                playerDataMap.put(Integer.toString(playerId2), player4);
                break;
            default:
                throw new IllegalArgumentException(response + playerId2);
        }

        return playerDataMap;
    }

}
