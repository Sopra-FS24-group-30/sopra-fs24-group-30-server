package ch.uzh.ifi.hase.soprafs24.logic.Game;

import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Effects.Getem;

import java.util.*;

public class Spaces {
    static Map<String, runFunc<?, ?>> runLandOns = new HashMap<>();

    static{
        runLandOns.put("2", Spaces::blue);
        runLandOns.put("3", Spaces::item);
        runLandOns.put("4", Spaces::card);
        runLandOns.put("5", Spaces::gambling);
        runLandOns.put("6", Spaces::catnami);
        runLandOns.put("7", Spaces::black);
        runLandOns.put("8", Spaces::red);
        runLandOns.put("12", Spaces::teleportToSpace49);
        runLandOns.put("13", Spaces::teleportToSpace13);
        runLandOns.put("14", Spaces::teleportToTheirStart);
        runLandOns.put("15", Spaces::sellAllItems);
        runLandOns.put("16", Spaces::rollAgainOrGet10Coins);
        runLandOns.put("17", Spaces::mustBuyItemOrCard);
        runLandOns.put("18", Spaces::stealOthersMoney);
        runLandOns.put("19", Spaces::nothing);
        runLandOns.put("20", Spaces::found20Money);
        runLandOns.put("21", Spaces::teleportToRandom);
        runLandOns.put("22", Spaces::getRandomStuff);
        runLandOns.put("23", Spaces::gift10Money);
        runLandOns.put("24", Spaces::sellAllCards);
        runLandOns.put("25", Spaces::getOthersCards);
        runLandOns.put("26", Spaces::surpriseMF);
        runLandOns.put("27", Spaces::swapCardsOrItems);
    }

    interface runFunc<T, U>{
        void apply(T arg1, U arg2);
    }

    private static int randomInt(int num){
        return (int) (Math.random()*num); //NOSONAR
    }


    public static void blue(Object player, Object players) {
        System.out.println("blui");
        Player currPlayer = (Player) player;
        currPlayer.addCash(+4);
    }
    public static void item(Object player, Object players) {
        System.out.println("itemis");
        Player currPlayer = (Player) player;
        int rando = randomInt(GameFlow.allItems.length);
        currPlayer.addItemNames(GameFlow.allItems[rando]);
    }
    public static void card(Object player, Object players) {
        System.out.println("cardis");
        Player currPlayer = (Player) player;
        List<String> allCards = new ArrayList<>(Getem.getCards().keySet());
        int rando = randomInt(allCards.size());
        currPlayer.addCardNames(allCards.get(rando));
    }
    public static void gambling(Object player, Object players) {
        //player loose all cash
        //player doubles all cash
        // or cards
        // or items
        //TODO
        System.out.println("kaChing");
    }
    public static void catnami(Object player, Object players) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        int x = randomInt(4);
        String pWin = currPlayer.getWinCondition();
        int mateId = currPlayer.getTeammateId().intValue();
        if (x==0){
            System.out.println("meow 0");
            currPlayer.addCash(+69);
        } else if (x==1){
            System.out.println("meow 1");
            currPlayer.setWinCondition(currPlayers[mateId-1].getWinCondition());
            currPlayers[mateId-1].setWinCondition(pWin);
        } else if (x==2){
            System.out.println("meow 2");
            List<Integer> pRand = new ArrayList<>();
            int randi = (currPlayer.getPlayerId().intValue() + mateId)/2;
            pRand.add(randi);
            pRand.add(currPlayers[randi-1].getTeammateId().intValue());
            int y = randomInt(2);
            String rWin = currPlayers[pRand.get(y)-1].getWinCondition();
            currPlayers[pRand.get(y)-1].setWinCondition(pWin);
            currPlayer.setWinCondition(rWin);
        } else{
            //TODO unused wincondi
            System.out.println("meow 3");
        }
    }
    public static void black(Object player, Object players) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        int x = randomInt(3);
        ArrayList<Long> allPosis = new ArrayList<>();
        int allCash = 0;
        for (Player p : currPlayers){
            allPosis.add(p.getPosition());
            allCash += p.getCash();
        }
        if (x==0){
            System.out.println("blacki 0");
            int cash = Math.min(69, currPlayer.getCash());
            currPlayer.addCash(-cash);
        } else if (x==1){
            System.out.println("blacki 1");
            int partial = allCash / 4;
            for (Player p : currPlayers){
                p.setCash(partial);
            }
        } else{
            System.out.println("blacki 2");
            Collections.shuffle(allPosis);
            for (Player p : currPlayers){
                int a = p.getPlayerId().intValue()-1;
                p.setPosition(allPosis.get(a));
            }
        }
    }
    public static void red(Object player, Object players) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        int x = randomInt(3);
        if (x==0){
            System.out.println("reddi 0");
            int cash = Math.min(10, currPlayer.getCash());
            currPlayer.addCash(-cash);
        } else if (x==1){
            System.out.println("reddi 1");
            for (Player p : currPlayers){
                int cash = Math.min(10, p.getCash());
                p.addCash(-cash);
            }
        } else {
            System.out.println("reddi 2");
            teleportToTheirStart(player, players);
        }

    }
    public static void teleportToSpace49(Object player, Object players) {
        System.out.println("tp49");
        Player currPlayer = (Player) player;
        currPlayer.setPosition(49L);
    }
    public static void teleportToSpace13(Object player, Object players) {
        System.out.println("tp13");
        Player currPlayer = (Player) player;
        currPlayer.setPosition(13L);
    }
    public static void teleportToTheirStart(Object player, Object players) {
        System.out.println("tpStart");
        Player currPlayer = (Player) player;
        int pId = currPlayer.getPlayerId().intValue();
        Long theirStart = GameFlow.findStart(pId);
        currPlayer.setPosition(theirStart);

    }
    public static void sellAllItems(Object player, Object players) {
        System.out.println("sellItem");
        Player currPlayer = (Player) player;
        Map<String, ArrayList<String>> allItems = getItemsByColor();
        for (String item : currPlayer.getItemNames()){
            if (allItems.get("bronze").contains(item)){
                currPlayer.addCash(+5);
            } else if (allItems.get("silver").contains(item)){
                currPlayer.addCash(+7);
            } else if (allItems.get("gold").contains(item)){
                currPlayer.addCash(+10);
            }
        }
        currPlayer.setItemNames(new ArrayList<>());
    }
    public static void rollAgainOrGet10Coins(Object player, Object players) {
        Player currPlayer = (Player) player;
        int x = randomInt(2);
        if (x==0){
            //TODO roll dice again
            System.out.println("diceOr10 0");
            GameWebSocketController.diceWalk();
        } else{
            System.out.println("diceOr10 1");
            currPlayer.addCash(+10);
        }
    }
    public static void mustBuyItemOrCard(Object player, Object players) {
        Player currPlayer = (Player) player;
        int cash = Math.min(15, currPlayer.getCash());
        if (cash>=15){
            System.out.println("buyCarTem 0");
            String it = GameFlow.randoItem();
            currPlayer.addItemNames(it);
        } else {
            System.out.println("buyCarTem 1");
            card(player, players);
        }
        currPlayer.addCash(-cash);
    }
    public static void stealOthersMoney(Object player, Object players) {
        System.out.println("10Cashall");
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        int cash = 0;
        for (Player p : currPlayers){
            if (!p.equals(player)){
                int maxi = Math.min(10, p.getCash());
                cash += maxi;
                p.addCash(-maxi);
            }
        }
        currPlayer.addCash(+cash);
    }
    public static void nothing(Object player, Object players) {
        //nothing, next player
    }
    public static void found20Money(Object player, Object players) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        int x = randomInt(3);
        int mateId = currPlayer.getTeammateId().intValue();
        if (x==0){
            System.out.println("20Cash 0");
            currPlayer.addCash(+20);
        }else if (x==1) {
            System.out.println("20Cash 1");
            currPlayers[mateId - 1].addCash(+20);
        }
    }
    public static void teleportToRandom(Object player, Object players) {
        System.out.println("tpRando");
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        int x = randomInt(2);
        int rando = randomInt(currPlayers.length);
        if (currPlayers[rando]==player){
            rando = (rando + 1 >= currPlayers.length) ? 1 : rando+1;
        }
        Long pPosi = currPlayer.getPosition();
        Long rPosi = currPlayers[rando].getPosition();
        currPlayer.setPosition(rPosi);
        if (x==0){
            currPlayers[rando].setPosition(pPosi);
        }
    }
    public static void getRandomStuff(Object player, Object players) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        int x = randomInt(3);
        if (x==0){
            System.out.println("randoStuff 0");
            int mateId = currPlayer.getTeammateId().intValue();
            currPlayer.addItemNames("TheBrotherAndCo");
            currPlayers[mateId-1].addItemNames("TheBrotherAndCo");
        } else if (x==1){
            System.out.println("randoStuff 1");
            card(player, players);
            card(player,players);
        } else {
            System.out.println("randoStuff 2");
            ArrayList<String> goldItems = getItemsByColor().get("gold");
            currPlayer.addItemNames(goldItems.get(randomInt(goldItems.size())));
        }
    }
    public static void gift10Money(Object player, Object players) {
        System.out.println("10cash");
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        int mateId = currPlayer.getTeammateId().intValue();
        int cash = Math.min(10, currPlayer.getCash());
        currPlayer.addCash(-cash);
        currPlayers[mateId-1].addCash(+cash);
    }
    public static void sellAllCards(Object player, Object players) {
        System.out.println("sellCard");
        Player currPlayer = (Player) player;
        Map<String, ArrayList<String>> allCards = getCardsByColor();
        for (String card : currPlayer.getCardNames()){
            if (allCards.get("bronze").contains(card)){
                currPlayer.addCash(+5);
            } else if (allCards.get("silver").contains(card)){
                currPlayer.addCash(+7);
            } else if (allCards.get("gold").contains(card)){
                currPlayer.addCash(+10);
            }
        }
        currPlayer.setCardNames(new ArrayList<>());
    }
    public static void getOthersCards(Object player, Object players) {
        System.out.println("getCardis");
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        for (Player p : currPlayers){
            if (p.getCardNames().size()==0){
                int cash = Math.min(5, p.getCash());
                p.addCash(-cash);
            } else {
                String cardname = p.getCardNames().get(0);
                currPlayer.addCardNames(cardname);
                p.removeCardNames(cardname);
            }
        }
    }
    public static void surpriseMF(Object player, Object players) {
        System.out.println("MEOW");
        catnami(player, players);
    }
    public static void swapCardsOrItems(Object player, Object players) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        int x = randomInt(2);
        int mateId = currPlayer.getTeammateId().intValue();
        if (x==0){
            System.out.println("swapCarTem 0");
            ArrayList<String> temp1 = currPlayer.getItemNames();
            currPlayer.setItemNames(currPlayers[mateId-1].getItemNames());
            currPlayers[mateId-1].setItemNames(temp1);
        }else{
            System.out.println("swapCarTem 1");
            ArrayList<String> temp2 = currPlayer.getCardNames();
            currPlayer.setCardNames(currPlayers[mateId-1].getCardNames());
            currPlayers[mateId-1].setCardNames(temp2);
        }
    }


    /**
     *
     * ITEM COLOR
     * CARD COLOR
     *
     */
    private static Map<String, ArrayList<String>> getItemsByColor(){
        ArrayList<String> bronze = new ArrayList<>(Arrays.asList("MagicMushroom", "TheBrotherAndCo")); // "TwoMushrooms", "PeaceImOut", "Fusion", "IceCreamChest", "WhatsThis"
        ArrayList<String> silver = new ArrayList<>(Arrays.asList("SuperMagicMushroom")); // "TreasureChest", "Stick", "ImOut", "MeowYou", "XBoxController", "BadWifi"
        ArrayList<String> goldes = new ArrayList<>(Arrays.asList("UltraMagicMushroom", "OnlyFansSub")); // "BestTradeDeal", "ItemsAreBelongToMe", "Confusion", "GoldenSnitch", "ChickyNuggie"
        return Map.ofEntries(Map.entry("bronze", bronze), Map.entry("silver", silver), Map.entry("gold", goldes));
    }
    private static Map<String, ArrayList<String>> getCardsByColor(){
        ArrayList<String> bronze = new ArrayList<>(Arrays.asList("B14", "B26", "B35", "B135", "B246", "B123", "B456", "B07"));
        ArrayList<String> silver = new ArrayList<>(Arrays.asList("S0", "S1", "S2", "S3", "S4", "S5", "S6", "S7"));
        ArrayList<String> goldes = new ArrayList<>(Arrays.asList("G13", "G26", "G45", "G04", "G37", "G1256"));
        return Map.ofEntries(Map.entry("bronze", bronze), Map.entry("silver", silver), Map.entry("gold", goldes));
    }
}
