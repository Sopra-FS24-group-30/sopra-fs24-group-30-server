package ch.uzh.ifi.hase.soprafs24.logic.Returns;

import java.util.ArrayList;

public class DiceData {

    ArrayList<Integer> results;

    public ArrayList<Integer> getResults() {
        return results;
    }

    public void setResults(ArrayList<Integer> results) {
        this.results = results;
    }

    public DiceData(ArrayList<Integer> results){
        this.results = results;
    }
}
