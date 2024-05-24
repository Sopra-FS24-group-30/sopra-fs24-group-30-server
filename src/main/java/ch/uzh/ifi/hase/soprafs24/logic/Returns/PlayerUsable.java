package ch.uzh.ifi.hase.soprafs24.logic.Returns;//NOSONAR

import java.util.ArrayList;

public class PlayerUsable {

    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> cards = new ArrayList<>();

    public ArrayList<String> getItems() {//NOSONAR
        return items;
    }

    public void setItems(ArrayList<String> items) {//NOSONAR
        this.items = items;
    }

    public ArrayList<String> getCards() {//NOSONAR
        return cards;
    }

    public void setCards(ArrayList<String> cards) {//NOSONAR
        this.cards = cards;
    }
}
