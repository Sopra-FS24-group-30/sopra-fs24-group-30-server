package ch.uzh.ifi.hase.soprafs24.logic.Returns.Helpers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CashData {

    @JsonProperty("1")
    private PlayerCash player2 = new PlayerCash();
    @JsonProperty("2")
    private PlayerCash player1 = new PlayerCash();
    @JsonProperty("3")
    private PlayerCash player3 = new PlayerCash();
    @JsonProperty("4")
    private PlayerCash player4 = new PlayerCash();


    public CashData(int player1Cash, int player2Cash, int player3Cash, int player4Cash){
        player1.setCash(player1Cash);
        player2.setCash(player2Cash);
        player3.setCash(player3Cash);
        player4.setCash(player4Cash);
    }
    public void setPlayer1Cash(int cash){
        player1.setCash(cash);
    }

    public void setPlayer2cash(int cash){
        player2.setCash(cash);
    }

    public void setPlayer3Cash(int cash){
        player3.setCash(cash);
    }

    public void setPlayer4Cash(int cash){
        player4.setCash(cash);
    }

}
