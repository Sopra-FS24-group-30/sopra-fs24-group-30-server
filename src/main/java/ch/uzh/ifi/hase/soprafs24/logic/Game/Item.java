package ch.uzh.ifi.hase.soprafs24.logic.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * One can acquire and use items during the game.
 * Items and what they do:
 * - MagicMushroom: This turn you roll 2 dice. If you roll doubles: +10 Cash.
 * - SuperMagicMushroom: This turn you roll 3 dice. If you roll triplets: +30 Cash
 * - UltraMagicMushroom: This turn you roll 4 dice. If you roll quadruplets: +69 Cash.
 * - OnlyFansAbo: Steal 7 coins from every other player (even your Teammate (simp))
 * - TreasureChest: Steal a random Item from a player of your choice.
 * - TheBrotherAndCo: Use this item to pass a gate, you can use this item at a gate, even if you have already used an item this turn.
 */
public class Item {
    private String itemName;

    public Item(String itemName) {
        this.itemName = itemName;
    }

    public static List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>();
        allItems.add(new Item("TheBrotherAndCo"));
        allItems.add(new Item("MagicMushroom"));
        allItems.add(new Item("SuperMagicMushroom"));
        allItems.add(new Item("UltraMagicMushroom"));
        allItems.add(new Item("OnlyFansAbo"));
        allItems.add(new Item("TreasureChest"));
        return allItems;
    }

    public String getItemName() {
        return itemName;
    }
    //Ambrosses art von class und three subclass how
    //class MushroomItem extends Item {//MagicMushroom, SuperMagicMushroom, UltraMagicMushroom}
    //class BrothaItem extends Item {//TheBrotherAndCo}
    //class OtherItem extends Item {//TreasureChest, OnlyFansAbo}
}
