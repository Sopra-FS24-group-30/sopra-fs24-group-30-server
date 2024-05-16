package ch.uzh.ifi.hase.soprafs24.logic.Returns;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class CashData {

    @JsonProperty("1")
    private PlayerCash player1 = new PlayerCash();
    @JsonProperty("2")
    private PlayerCash player2 = new PlayerCash();
    @JsonProperty("3")
    private PlayerCash player3 = new PlayerCash();
    @JsonProperty("4")
    private PlayerCash player4 = new PlayerCash();


    public void setPlayersNewCash(int cash1, int cash2, int cash3, int cash4){
        player1.setNewAmountOfMoney(cash1);
        player2.setNewAmountOfMoney(cash2);
        player3.setNewAmountOfMoney(cash3);
        player4.setNewAmountOfMoney(cash4);
    }

    public void setPlayersChangeAmount(int cash1, int cash2, int cash3, int cash4){
        player1.setChangeAmountOfMoney(cash1);
        player2.setChangeAmountOfMoney(cash2);
        player3.setChangeAmountOfMoney(cash3);
        player4.setChangeAmountOfMoney(cash4);
    }

    public void setPlayerAmountAndUpdate(int playerId, int newCash, int cashChange){
        switch (playerId){ //NOSONAR
            case 1:
                player1.setNewAmountOfMoney(newCash);
                player1.setChangeAmountOfMoney(cashChange);
                break;
            case 2:
                player2.setNewAmountOfMoney(newCash);
                player2.setChangeAmountOfMoney(cashChange);
                break;
            case 3:
                player3.setNewAmountOfMoney(newCash);
                player3.setChangeAmountOfMoney(cashChange);
                break;
            case 4:
                player4.setNewAmountOfMoney(newCash);
                player4.setChangeAmountOfMoney(cashChange);
                break;
        }
    }

    public PlayerCash getPlayerCash(int playerId) {
        switch (playerId) {
            case 1 -> {return player1;}
            case 2 -> {return player2;}
            case 3 -> {return player3;}
            case 4 -> {return player4;}
            default -> {return null;}
        }
    }

    public String getPlayerCashJson(int playerId) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(Map.of(String.valueOf(playerId), getPlayerCash(playerId)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPlayerCashJson(int playerId1, int palayerId2) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, PlayerCash> playerCashMap = new HashMap<>();
        PlayerCash playerCash1 = getPlayerCash(playerId1);
        playerCashMap.put(String.valueOf(playerId1), playerCash1);
        PlayerCash playerCash2 = getPlayerCash(playerId1);
        playerCashMap.put(String.valueOf(playerId1), playerCash2);
        try {
            return objectMapper.writeValueAsString(playerCashMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setPlayer1newAmount(int cash){
        player1.setNewAmountOfMoney(cash);
    }

    public void setPlayer2newAmount(int cash){
        player2.setNewAmountOfMoney(cash);
    }

    public void setPlayer3newAmount(int cash){
        player3.setNewAmountOfMoney(cash);
    }

    public void setPlayer4newAmount(int cash){
        player4.setNewAmountOfMoney(cash);
    }

    public void setPlayer1changeAmount(int cash){
        player1.setChangeAmountOfMoney(cash);
    }

    public void setPlayer2changeAmount(int cash){
        player2.setChangeAmountOfMoney(cash);
    }

    public void setPlayer3changeAmount(int cash){
        player3.setChangeAmountOfMoney(cash);
    }

    public void setPlayer4changeAmount(int cash){
        player4.setChangeAmountOfMoney(cash);
    }

}
