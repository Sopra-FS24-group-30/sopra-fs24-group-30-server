package ch.uzh.ifi.hase.soprafs24.logic.Returns.Helpers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class UsableData {

    @JsonProperty("1")
    private PlayerUsable player1Us = new PlayerUsable();
    @JsonProperty("2")
    private PlayerUsable player2Us = new PlayerUsable();
    @JsonProperty("3")
    private PlayerUsable player3Us = new PlayerUsable();
    @JsonProperty("4")
    private PlayerUsable player4Us = new PlayerUsable();


    public void setItems(ArrayList<String> player1, ArrayList<String> player2, ArrayList<String> player3, ArrayList<String> player4){
        player1Us.setItems(player1);
        player2Us.setItems(player2);
        player3Us.setItems(player3);
        player4Us.setItems(player4);
    }

    public void setCards(ArrayList<String> player1, ArrayList<String> player2, ArrayList<String> player3, ArrayList<String> player4){
        player1Us.setCards(player1);
        player2Us.setCards(player2);
        player3Us.setCards(player3);
        player4Us.setCards(player4);
    }
}
