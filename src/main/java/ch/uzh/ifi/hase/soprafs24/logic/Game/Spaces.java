package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Effects.Getem;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.UsableData;
import ch.uzh.ifi.hase.soprafs24.service.GameManagementService;

import java.util.*;

public class Spaces {
    static Map<String, runFunc<?, ?, ?>> runLandOns = new HashMap<>();

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
        runLandOns.put("16", Spaces::getMushroom);
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

    interface runFunc<T, U, V>{ //NOSONAR
        void apply(T arg1, U arg2, V arg3);
    }

    private static int randomInt(int num){
        return (int) (Math.random()*num); //NOSONAR
    }


    public static void blue(Object player, Object players, Object gameId) {
        System.out.println("blui");
        Player currPlayer = (Player) player;
        Long gaId = (Long) gameId;
        currPlayer.addCash(+4);
    }
    public static void item(Object player, Object players, Object gameId) {
        System.out.println("itemis");
        Player currPlayer = (Player) player;
        Long gaId = (Long) gameId;
        int rando = randomInt(GameFlow.allItems.length);
        currPlayer.addItemNames(GameFlow.allItems[rando]);
    }
    public static void card(Object player, Object players, Object gameId) {
        System.out.println("cardis");
        Player currPlayer = (Player) player;
        Long gaId = (Long) gameId;
        List<String> allCards = new ArrayList<>(Getem.getCards().keySet());
        int rando = randomInt(allCards.size());
        currPlayer.addCardNames(allCards.get(rando));
    }
    public static void gambling(Object player, Object players, Object gameId) {
        System.out.println("kaChing");
        Player currPlayer = (Player) player;
        Long gaId = (Long) gameId;
        int x = randomInt(6);
        switch (x) {
            case 0 -> {
                currPlayer.addCash(currPlayer.getCash());
            }
            case 1 -> {
                currPlayer.setCash(0);
            }
            case 2 -> {
                List<String> allIts = Getem.getItems().keySet().stream().toList();
                int y = allIts.size();
                int z = currPlayer.getItemNames().size();
                for (int i=0; i<z; i++){
                    currPlayer.addItemNames(allIts.get(randomInt(y)));
                }
            }
            case 3 -> {
                currPlayer.setItemNames(new ArrayList<>());
            }
            case 4 -> {
                List<String> allCas = Getem.getCards().keySet().stream().toList();
                int y = allCas.size();
                int z = currPlayer.getCardNames().size();
                for (int i=0; i<z; i++){
                    currPlayer.addItemNames(allCas.get(randomInt(y)));
                }
            }
            case 5 -> {
                currPlayer.setCardNames(new ArrayList<>());
            }
        }
    }
    public static void gambling(GameFlow gameflow){
        Player[] players = gameflow.getPlayers();
        Player currPlayer = players[gameflow.getTurnPlayerId().intValue()-1];
        int random = randomInt(2);
        boolean win = random < 2;
        random = randomInt(2);
        boolean item = random < 2;
        if(item) {
            if (win) {
                ArrayList<String> items = new ArrayList<>();
                for(int i=0;i<currPlayer.getItemNames().size();i++){
                    items.add(GameFlow.randoItem());
                }
                currPlayer.addItemNames(items);
            }else {
                currPlayer.setItemNames(new ArrayList<>());
            }
        }else {
            //cards
            if (win) {
                ArrayList<String> cards = new ArrayList<>();
                for(int i=0;i<currPlayer.getCardNames().size();i++){
                    cards.add(GameFlow.randoItem());
                }
                currPlayer.addCardNames(cards);
            }else {
                currPlayer.setCardNames(new ArrayList<>());
            }
        }
        UsableData usableData = UsableData.prepateDataItems(gameflow);
        GameWebSocketController.returnUsables(usableData, gameflow.getGameId());
    }

    public static void catnami(Object player, Object players, Object gameId) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
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
            System.out.println("meow 3");
            currPlayers[mateId-1].setCash(0);
        }
    }
    public static void black(Object player, Object players, Object gameId) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
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
    public static void red(Object player, Object players, Object gameId) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
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
            teleportToTheirStart(player, players, gameId);
        }

    }
    public static void teleportToSpace49(Object player, Object players, Object gameId) {
        System.out.println("tp49");
        Player currPlayer = (Player) player;
        Long gaId = (Long) gameId;
        currPlayer.setPosition(49L);
    }
    public static void teleportToSpace13(Object player, Object players, Object gameId) {
        System.out.println("tp13");
        Player currPlayer = (Player) player;
        Long gaId = (Long) gameId;
        currPlayer.setPosition(13L);
    }
    public static void teleportToTheirStart(Object player, Object players, Object gameId) {
        System.out.println("tpStart");
        Player currPlayer = (Player) player;
        Long gaId = (Long) gameId;
        int pId = currPlayer.getPlayerId().intValue();
        Long theirStart = GameFlow.findStart(pId);
        currPlayer.setPosition(theirStart);

    }
    public static void sellAllItems(Object player, Object players, Object gameId) {
        System.out.println("sellItem");
        Player currPlayer = (Player) player;
        Long gaId = (Long) gameId;
        Map<String, ArrayList<String>> allItems = getItemsByColor();
        for (String item : currPlayer.getItemNames()){
            if (allItems.get("bronze").contains(item)){ //NOSONAR
                currPlayer.addCash(+5);
            } else if (allItems.get("silver").contains(item)){ //NOSONAR
                currPlayer.addCash(+7);
            } else if (allItems.get("gold").contains(item)){ //NOSONAR
                currPlayer.addCash(+10);
            }
        }
        currPlayer.setItemNames(new ArrayList<>());
    }
    public static void getMushroom(Object player, Object players, Object gameId) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
        int x = randomInt(2);
        if (x==0){
            System.out.println("mushishi 0");
            currPlayer.addCardNames("MagicMushroom");
        } else{
            System.out.println("mushishi 1");
            for (Player p : currPlayers){
                if (!p.equals(currPlayer)){
                    p.addCardNames("MagicMushroom");
                }
            }
        }
    }
    public static void mustBuyItemOrCard(Object player, Object players, Object gameId) {
        Player currPlayer = (Player) player;
        Long gaId = (Long) gameId;
        int cash = Math.min(15, currPlayer.getCash());
        if (cash>=15){
            System.out.println("buyCarTem 0");
            String it = GameFlow.randoItem();
            currPlayer.addItemNames(it);
        } else {
            System.out.println("buyCarTem 1");
            card(player, players, gameId);
        }
        currPlayer.addCash(-cash);
    }
    public static void stealOthersMoney(Object player, Object players, Object gameId) {
        System.out.println("10Cashall");
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
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
    public static void nothing(Object player, Object players, Object gameId) {
        //nothing, next player
    }
    public static void found20Money(Object player, Object players, Object gameId) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
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
    public static void teleportToRandom(Object player, Object players, Object gameId) {
        System.out.println("tpRando");
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
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
    public static void getRandomStuff(Object player, Object players, Object gameId) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
        int x = randomInt(3);
        if (x==0){
            System.out.println("randoStuff 0");
            int mateId = currPlayer.getTeammateId().intValue();
            currPlayer.addItemNames("TheBrotherAndCo"); //NOSONAR
            currPlayers[mateId-1].addItemNames("TheBrotherAndCo");
        } else if (x==1){
            System.out.println("randoStuff 1");
            card(player, players, gameId);
            card(player,players, gameId);
        } else {
            System.out.println("randoStuff 2");
            ArrayList<String> goldItems = getItemsByColor().get("gold");
            currPlayer.addItemNames(goldItems.get(randomInt(goldItems.size())));
        }
    }
    public static void gift10Money(Object player, Object players, Object gameId) {
        System.out.println("10cash");
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
        int mateId = currPlayer.getTeammateId().intValue();
        int cash = Math.min(10, currPlayer.getCash());
        currPlayer.addCash(-cash);
        currPlayers[mateId-1].addCash(+cash);
    }
    public static void sellAllCards(Object player, Object players, Object gameId) {
        System.out.println("sellCard");
        Player currPlayer = (Player) player;
        Long gaId = (Long) gameId;
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
    public static void getOthersCards(Object player, Object players, Object gameId) {
        System.out.println("getCardis");
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
        for (Player p : currPlayers){
            if (p.getCardNames().isEmpty()){
                int cash = Math.min(5, p.getCash());
                p.addCash(-cash);
            } else {
                String cardname = p.getCardNames().get(0);
                currPlayer.addCardNames(cardname);
                p.removeCardNames(cardname);
            }
        }
    }
    public static void surpriseMF(Object player, Object players, Object gameId) {
        System.out.println("MEOW");
        catnami(player, players, gameId);
    }
    public static void swapCardsOrItems(Object player, Object players, Object gameId) {
        Player currPlayer = (Player) player;
        Player[] currPlayers = (Player[]) players;
        Long gaId = (Long) gameId;
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

    /**
     *
     * WS MSG DICT
     *
     */
    private static Map<String, Object> toMoney(Player player){
        return Collections.emptyMap();
    }
    private static Map<String, Object> toUsable(Player player){
        return Collections.emptyMap();
    }
    private static Map<String, Object> toWinCondition(Player player){
        return Collections.emptyMap();
    }
    private static Map<String, Object> toMove(Player player){
        return Collections.emptyMap();
    }
}
