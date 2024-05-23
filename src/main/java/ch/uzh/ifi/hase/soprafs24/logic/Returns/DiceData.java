package ch.uzh.ifi.hase.soprafs24.logic.Returns;//NOSONAR

import java.util.ArrayList;

public class DiceData {

    ArrayList<Integer> results;

    public ArrayList<Integer> getResults() {//NOSONAR
        return results;
    }

    public void setResults(ArrayList<Integer> results) {//NOSONAR
        this.results = results;
    }

    public DiceData(ArrayList<Integer> results){//NOSONAR
        this.results = results;
    }
}
