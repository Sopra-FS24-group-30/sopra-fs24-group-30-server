package ch.uzh.ifi.hase.soprafs24.logic.Game;

import java.util.HashMap;
import java.util.Map;

public class Spaces {
    private static Map<String, String> landOns = new HashMap<>();
    private static Map<String,runFunc> runLandOns = new HashMap<>();

    static {
        landOns.put("1", "goal");
        landOns.put("2", "blue");
        landOns.put("3", "item");
        landOns.put("4", "card");
        landOns.put("5", "gambling");
        landOns.put("6", "catnami");
        landOns.put("7", "black");
        landOns.put("8", "red");
        landOns.put("9", "junction");
        landOns.put("10", "gate");
        landOns.put("11", "specialItem");
        landOns.put("12", "teleportToSpace49");
        landOns.put("13", "teleportToSpace13");
        landOns.put("14", "teleportToTheirStart");
        landOns.put("15", "sellAllItems");
        landOns.put("16", "rollAgainOrGet10Coins");
        landOns.put("17", "mustBuyItemOrCard");
        landOns.put("18", "stealOthersMoney");
        landOns.put("19", "nothing");
        landOns.put("20", "found20Money");
        landOns.put("21", "teleportToRandom");
        landOns.put("22", "getRandomStuff");
        landOns.put("23", "gift10Money");
        landOns.put("24", "sellAllCards");
        landOns.put("25", "getOthersCards");
        landOns.put("26", "surpriseMF");
        landOns.put("27", "swapCardsOrItems");
        landOns.put("28", "start");
    }

    static{
        runLandOns.put("1", (player) -> goal(player));
        runLandOns.put("2", (player) -> blue(player));
        runLandOns.put("3", (player) -> item(player));
        runLandOns.put("4", (player) -> card(player));
        runLandOns.put("5", (player) -> gambling(player));
        runLandOns.put("6", (player) -> catnami(player));
        runLandOns.put("7", (player) -> black(player));
        runLandOns.put("8", (player) -> red(player));
        runLandOns.put("9", (player) -> junction(player));
        runLandOns.put("10", (player) -> gate(player));
        runLandOns.put("11", (player) -> specialItem(player));
        runLandOns.put("12", (player) -> teleportToSpace49(player));
        runLandOns.put("13", (player) -> teleportToSpace13(player));
        runLandOns.put("14", (player) -> teleportToTheirStart(player));
        runLandOns.put("15", (player) -> sellAllItems(player));
        runLandOns.put("16", (player) -> rollAgainOrGet10Coins(player));
        runLandOns.put("17", (player) -> mustBuyItemOrCard(player));
        runLandOns.put("18", (player) -> stealOthersMoney(player));
        runLandOns.put("19", (player) -> nothing(player));
        runLandOns.put("20", (player) -> found20Money(player));
        runLandOns.put("21", (player) -> teleportToRandom(player));
        runLandOns.put("22", (player) -> getRandomStuff(player));
        runLandOns.put("23", (player) -> gift10Money(player));
        runLandOns.put("24", (player) -> sellAllCards(player));
        runLandOns.put("25", (player) -> getOthersCards(player));
        runLandOns.put("26", (player) -> surpriseMF(player));
        runLandOns.put("27", (player) -> swapCardsOrItems(player));
        runLandOns.put("28", (player) -> start(player));
    }

    public static Map<String, String> getLandOns(){
        return landOns;
    }

    interface runFunc{
        void run(Player player);
    }
    public static void goal(Player player) {}
    public static void blue(Player player) {}
    public static void item(Player player) {}
    public static void card(Player player) {}
    public static void gambling(Player player) {}
    public static void catnami(Player player) {}
    public static void black(Player player) {}
    public static void red(Player player) {}
    public static void junction(Player player) {}
    public static void gate(Player player) {}
    public static void specialItem(Player player) {}
    public static void teleportToSpace49(Player player) {}
    public static void teleportToSpace13(Player player) {}
    public static void teleportToTheirStart(Player player) {}
    public static void sellAllItems(Player player) {}
    public static void rollAgainOrGet10Coins(Player player) {}
    public static void mustBuyItemOrCard(Player player) {}
    public static void stealOthersMoney(Player player) {}
    public static void nothing(Player player) {}
    public static void found20Money(Player player) {}
    public static void teleportToRandom(Player player) {}
    public static void getRandomStuff(Player player) {}
    public static void gift10Money(Player player) {}
    public static void sellAllCards(Player player) {}
    public static void getOthersCards(Player player) {}
    public static void surpriseMF(Player player) {}
    public static void swapCardsOrItems(Player player) {}
    public static void start(Player player) {}

}
