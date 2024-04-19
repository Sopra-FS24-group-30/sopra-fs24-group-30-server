package ch.uzh.ifi.hase.soprafs24.logic.Game;

/**
 * Items and what they do:
 * - MagicMushroom: This turn you roll 2 dice. If you roll doubles: +10 Cash.
 * - SuperMagicMushroom: This turn you roll 3 dice. If you roll triplets: +30 Cash
 * - UltraMagicMushroom: This turn you roll 4 dice. If you roll quadruplets: +69 Cash.
 * - OnlyFansAbo: Steal 7 coins from every other player (even your Teammate (simp))
 * - TreasureChest: Steal a random Item from a player of your choice.
 * - TheBrotherAndCo: Use this item to pass a gate, you can use this item at a gate, even if you have already used an item this turn.
 */
public class Item {

    private static final String[] goldItems = {"UltraMagicMushroom", "OnlyFansAbo"};
    private static final String[] silverItems = {"SuperMagicMushroom", "TreasureChest"};
    private static final String[] bronzeItems = {"MagicMushroom", "TheBrotherAndCo"};
}
