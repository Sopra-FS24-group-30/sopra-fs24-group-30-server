package ch.uzh.ifi.hase.soprafs24.logic.Returns;

import java.util.ArrayList;

public class Dice implements ReturnEffect{

    private final String type = this.getClass().getName();
    private ArrayList<String> data = new ArrayList<>();

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }
}
