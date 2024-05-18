package ch.uzh.ifi.hase.soprafs24.logic.Returns;

import java.util.ArrayList;

public class PlayerMove {

    private ArrayList<Long> spaces;
    //amount of moves left in beginning
    private int moves = 0;
    private String spaceColour;

    public ArrayList<Long> getSpaces() {
        return spaces;
    }

    public void setSpaces(ArrayList<Long> spaces) {
        this.spaces = spaces;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public String getSpaceColour() {
        return spaceColour;
    }

    public void setSpaceColour(String spaceColour) {
        this.spaceColour = spaceColour;
    }
}
