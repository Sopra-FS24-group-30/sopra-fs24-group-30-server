package ch.uzh.ifi.hase.soprafs24.logic.Returns.Helpers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class MoveData {

    @JsonProperty("1")
    private PlayerMove player1 = new PlayerMove();
    @JsonProperty("2")
    private PlayerMove player2 = new PlayerMove();
    @JsonProperty("3")
    private PlayerMove player3 = new PlayerMove();
    @JsonProperty("4")
    private PlayerMove player4 = new PlayerMove();

    public MoveData(ArrayList<Long> playerPos1, ArrayList<Long> playerPos2, ArrayList<Long> playerPos3, ArrayList<Long> playerPos4){
        this.player1.setSpaces(playerPos1);
        this.player2.setSpaces(playerPos2);
        this.player3.setSpaces(playerPos3);
        this.player4.setSpaces(playerPos4);
    }

    //TODO add change method

}
