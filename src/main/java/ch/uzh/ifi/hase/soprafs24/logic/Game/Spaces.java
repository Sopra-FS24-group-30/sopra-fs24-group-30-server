package ch.uzh.ifi.hase.soprafs24.logic.Game;

import java.util.*;

public class Spaces {
    static Map<String, runFunc<?, ?>> runLandOns = new HashMap<>();

    static{
        runLandOns.put("1", (Player player, Player[] players) -> goal(player, players));
        runLandOns.put("2",(Player player, Player[] players) -> blue(player, players));
        runLandOns.put("3",(Player player, Player[] players) -> item(player, players));
        runLandOns.put("4",(Player player, Player[] players) -> card(player, players));
        runLandOns.put("5",(Player player, Player[] players) -> gambling(player, players));
        runLandOns.put("6", (Player player, Player[] players) -> catnami(player, players));
        runLandOns.put("7",(Player player, Player[] players) -> black(player, players));
        runLandOns.put("8",(Player player, Player[] players) -> red(player, players));
        runLandOns.put("9",(Player player, Player[] players) -> junction(player, players));
        runLandOns.put("10",(Player player, Player[] players) -> gate(player, players));
        runLandOns.put("11",(Player player, Player[] players) -> specialItem(player, players));
        runLandOns.put("12",(Player player, Player[] players) -> teleportToSpace49(player, players));
        runLandOns.put("13",(Player player, Player[] players) -> teleportToSpace13(player, players));
        runLandOns.put("14",(Player player, Player[] players) -> teleportToTheirStart(player, players));
        runLandOns.put("15",(Player player, Player[] players) -> sellAllItems(player, players));
        runLandOns.put("16",(Player player, Player[] players) -> rollAgainOrGet10Coins(player, players));
        runLandOns.put("17",(Player player, Player[] players) -> mustBuyItemOrCard(player, players));
        runLandOns.put("18",(Player player, Player[] players) -> stealOthersMoney(player, players));
        runLandOns.put("19",(Player player, Player[] players) -> nothing(player, players));
        runLandOns.put("20",(Player player, Player[] players) -> found20Money(player, players));
        runLandOns.put("21",(Player player, Player[] players) -> teleportToRandom(player, players));
        runLandOns.put("22",(Player player, Player[] players) -> getRandomStuff(player, players));
        runLandOns.put("23",(Player player, Player[] players) -> gift10Money(player, players));
        runLandOns.put("24",(Player player, Player[] players) -> sellAllCards(player, players));
        runLandOns.put("25",(Player player, Player[] players) -> getOthersCards(player, players));
        runLandOns.put("26",(Player player, Player[] players) -> surpriseMF(player, players));
        runLandOns.put("27",(Player player, Player[] players) -> swapCardsOrItems(player, players));
        runLandOns.put("28",(Player player, Player[] players) -> start(player, players));
    }

    interface runFunc<T, U>{
        void apply(T arg1, U arg2);
    }

    public static void goal(Player player, Player[] players) {
        //skip?
    }
    public static void blue(Player player, Player[] players) {
        //player get 4 cash
    }
    public static void item(Player player, Player[] players) {
        //player gets 1 item
    }
    public static void card(Player player, Player[] players) {
        //player gets 1 card
    }
    public static void gambling(Player player, Player[] players) {
        //player loose all cash
        //player doubles all cash
        // or cards
        // or items
    }
    public static void catnami(Player player, Player[] players) {
        //player get 69 cash
        //player swap wincondi with teammate
        //player swap wincondi with random enemy
        //player swap wincondi with unused

        System.out.println("meow");
        List<WinCondition> allWCs = new ArrayList<>();
        for (Player p : players){
            allWCs.add(p.getWinCondition());
        }
        Collections.shuffle(allWCs);
        int i=0;
        for (Player p : players){
            p.setWinCondition(allWCs.get(i));
            i++;
        }

    }
    public static void black(Player player, Player[] players) {
        //swap posi of every player
        //lose 69 cash
        //evenly distribute all cash
    }
    public static void red(Player player, Player[] players) {
        //player lose 10 cash
        //everyone lose 10 cash
        //teleport player to their start
    }
    public static void junction(Player player, Player[] players) {
        //skip?
    }
    public static void gate(Player player, Player[] players) {
        //skip?
    }
    public static void specialItem(Player player, Player[] players) {
        //skip?
    }
    public static void teleportToSpace49(Player player, Player[] players) {
        //teleport player to 49
    }
    public static void teleportToSpace13(Player player, Player[] players) {
        //teleport player to 13
    }
    public static void teleportToTheirStart(Player player, Player[] players) {
        //teleport player to their start
    }
    public static void sellAllItems(Player player, Player[] players) {
        //for each item (lose all items)
        // +5 cash bronze
        // +7 cash silver
        // +10 cash gold
    }
    public static void rollAgainOrGet10Coins(Player player, Player[] players) {
        //roll dice again
        //player get 10 cash
    }
    public static void mustBuyItemOrCard(Player player, Player[] players) {
        //player lose max 15 cash
        //if player.cash > 15: player gets 1 item
        //if player.cash < 15: player gets 1 card
    }
    public static void stealOthersMoney(Player player, Player[] players) {
        //others lose max 10 cash each
        //player gets that money
    }
    public static void nothing(Player player, Player[] players) {
        //nothing, next player
    }
    public static void found20Money(Player player, Player[] players) {
        //player gets 20 cash
        //teammate gets 20 cash
        //nothing, next player
    }
    public static void teleportToRandom(Player player, Player[] players) {
        //player teleport to random player
        //player swaps posi with random player
    }
    public static void getRandomStuff(Player player, Player[] players) {
        //player and teammate get Bro
        //player gets 1 gold item
        //player gets 2 cards
    }
    public static void gift10Money(Player player, Player[] players) {
        //player loses max 10 cash
        //teammate gets that cash
    }
    public static void sellAllCards(Player player, Player[] players) {
        //for each card (lose all cards)
        // +5 cash bronze
        // +7 cash silver
        // +10 cash gold
    }
    public static void getOthersCards(Player player, Player[] players) {
        //others lose 1 card
        // if no card then -5 cash
        //player gets those cards, but not money
    }
    public static void surpriseMF(Player player, Player[] players) {
        //player get 69 cash
        //player swap wincondi with teammate
        //player swap wincondi with random enemy
        //player swap wincondi with unused
    }
    public static void swapCardsOrItems(Player player, Player[] players) {
        //swap all cards with teammate
        //swap all items with teammate
    }
    public static void start(Player player, Player[] players) {
        //skip?
    }

}
