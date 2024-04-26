package ch.uzh.ifi.hase.soprafs24.logic.Returns.Helpers;

import java.util.ArrayList;

public class PlayerUsable {

    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> cards = new ArrayList<>();

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public ArrayList<String> getCards() {
        return cards;
    }

    public void setCards(ArrayList<String> cards) {
        this.cards = cards;
    }
}
