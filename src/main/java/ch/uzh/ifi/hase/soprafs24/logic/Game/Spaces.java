package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Constant.GamblingChoice;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.CashData;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.MoveData;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.UsableData;

import java.util.*;

public class Spaces {
    Map<String, runFunc<GameFlow>> runLandOns = new HashMap<>();

    public Spaces() {
        runLandOns.put("2", this::blue);
        runLandOns.put("3", this::item);
        runLandOns.put("4", this::card);
        runLandOns.put("5", this::gambling);
        runLandOns.put("6", this::catnami);
        runLandOns.put("7", this::black);
        runLandOns.put("8", this::red);
        runLandOns.put("12", this::teleportToSpace49);
        runLandOns.put("13", this::teleportToSpace13);
        runLandOns.put("14", this::teleportToTheirStart);
        runLandOns.put("15", this::sellAllItems);
        runLandOns.put("16", this::getMushroom);
        runLandOns.put("17", this::mustBuyItemOrCard);
        runLandOns.put("18", this::stealOthersMoney);
        runLandOns.put("19", this::nothing);
        runLandOns.put("20", this::found20Money);
        runLandOns.put("21", this::teleportToRandom);
        runLandOns.put("22", this::getRandomStuff);
        runLandOns.put("23", this::gift10Money);
        runLandOns.put("24", this::sellAllCards);
        runLandOns.put("25", this::getOthersCards);
        runLandOns.put("26", this::surpriseMF);
        runLandOns.put("27", this::swapCardsOrItems);
    }

    interface runFunc<T>{ //NOSONAR
        void apply(T arg1);
    }

    public int randomInt(int num){
        return (int) (Math.random()*num); //NOSONAR
    }

    public void blue(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        currPlayer.addCash(4);
        toCash(currPlayer, 4, gameFlow);
    }

    public void item(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        String it = GameFlow.randoItem();
        currPlayer.addItemNames(it);
        toUsable(gameFlow, currPlayer);
    }
    public void card(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        String it = GameFlow.randoCard();
        currPlayer.addCardNames(it);
        toUsable(gameFlow, currPlayer);
    }
    public void gambling(GameFlow gameFlow){ //NOSONAR
        Player[] players = gameFlow.getPlayers();
        Player currPlayer = players[gameFlow.getTurnPlayerId().intValue()-1];
        int random = randomInt(2);
        boolean win = random < 2;
        GamblingChoice gamblingChoice = GamblingChoice.getGamblingChoice();
        switch(gamblingChoice) {
            case ITEM:
                if (win) {
                    ArrayList<String> items = new ArrayList<>();
                    for(int i=0;i<currPlayer.getItemNames().size();i++){
                        items.add(GameFlow.randoItem());
                    }
                    currPlayer.addItemNames(items);
                }else {
                    currPlayer.setItemNames(new ArrayList<>());
                }
                toUsable(gameFlow, currPlayer);
                break;
            case CARD:
                if (win) {
                    ArrayList<String> cards = new ArrayList<>();
                    for(int i=0;i<currPlayer.getCardNames().size();i++){
                        cards.add(GameFlow.randoCard());
                    }
                    currPlayer.addCardNames(cards);
                }else {
                    currPlayer.setCardNames(new ArrayList<>());
                }
                toUsable(gameFlow, currPlayer);
                break;
            case CASH:
                int change;
                if(win){
                    change = currPlayer.getCash();
                    currPlayer.setCash(currPlayer.getCash()*2);
                }else{
                    change = currPlayer.getCash() * -1;
                    currPlayer.setCash(0);
                }
                toCash(currPlayer, change, gameFlow);
                break;
        }

    }
    public void catnami(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayer(gameFlow.getTurnPlayerId().intValue());
        Player[] currPlayers = gameFlow.getPlayers();
        int x = randomInt(4);
        String pWin = currPlayer.getWinCondition();
        int mateId = currPlayer.getTeammateId().intValue();
        if (x==0){
            currPlayer.addCash(+69);
            toCash(currPlayer, 69, gameFlow);
        } else if (x==1){
            currPlayer.setWinCondition(currPlayers[mateId-1].getWinCondition());
            currPlayers[mateId-1].setWinCondition(pWin);
            toWinCondi(currPlayer, currPlayers[mateId-1], gameFlow);
        } else if (x==2){
            List<Integer> pRand = new ArrayList<>();
            int randi = (currPlayer.getPlayerId().intValue() + mateId)/2;
            pRand.add(randi);
            pRand.add(currPlayers[randi-1].getTeammateId().intValue());
            int y = randomInt(2);
            String rWin = currPlayers[pRand.get(y)-1].getWinCondition();
            currPlayers[pRand.get(y)-1].setWinCondition(pWin);
            currPlayer.setWinCondition(rWin);
            toWinCondi(currPlayer, currPlayers[pRand.get(y)-1], gameFlow);
        } else{
            int currCashMate = currPlayers[mateId-1].getCash();
            currPlayers[mateId-1].setCash(0);
            currPlayers[mateId-1].addLostCash(currCashMate);
            toCash(currPlayer, -currCashMate, gameFlow);
        }
    }
    public void black(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Player[] currPlayers = gameFlow.getPlayers();
        int x = randomInt(3);
        ArrayList<Long> allPosis = new ArrayList<>();
        int allCash = 0;
        for (Player p : currPlayers){
            allPosis.add(p.getPosition());
            allCash += p.getCash();
        }
        if (x==0){
            int cash = Math.min(69, currPlayer.getCash());
            currPlayer.addCash(-cash);
            toCash(currPlayer, -cash, gameFlow);
        } else if (x==1){
            int partial = allCash / 4;
            int initial;
            int delta;
            CashData cashData = new CashData(gameFlow);
            for ( Player p : currPlayers){
                initial = p.getCash();
                p.setCash(partial);
                delta = partial-initial;
                if (delta < 0){
                    p.addLostCash(-delta);
                }
                cashData.setPlayerAmountAndUpdate(p.getPlayerId().intValue(), p.getCash(), delta);
            }
            GameWebSocketController.returnMoney(cashData, gameFlow.getGameId());
        } else{
            Collections.shuffle(allPosis);
            for (Player p: currPlayers){
                p.setPosition(allPosis.get(p.getPlayerId().intValue()-1));
            }
            toMoveTp(currPlayers[0],currPlayers[1], currPlayers[2], currPlayers[3], gameFlow);
        }
    }
    public void red(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Player[] currPlayers = gameFlow.getPlayers();
        int x = randomInt(3);
        if (x==0){
            int cash = Math.min(10, currPlayer.getCash());
            currPlayer.addCash(-cash);
            toCash(currPlayer, -cash, gameFlow);
        } else if (x==1){
            CashData cashData = new CashData(gameFlow);
            for (Player p : currPlayers){
                int cash = Math.min(10, p.getCash());
                p.addCash(-cash);
                p.addLostCash(cash);
                cashData.setPlayerAmountAndUpdate(p.getPlayerId().intValue(), p.getCash(), -cash);
            }
            GameWebSocketController.returnMoney(cashData, gameFlow.getGameId());
        } else {
            teleportToTheirStart(gameFlow);
        }
    }
    public void teleportToSpace49(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        currPlayer.setPosition(49L);
        toMoveTp(currPlayer, gameFlow);
    }
    public void teleportToSpace13(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        currPlayer.setPosition(13L);
        toMoveTp(currPlayer, gameFlow);
    }
    public void teleportToTheirStart(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        int pId = currPlayer.getPlayerId().intValue();
        Long theirStart = GameFlow.findStart(pId);
        currPlayer.setPosition(theirStart);
        toMoveTp(currPlayer, gameFlow);
    }
    public void sellAllItems(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Map<String, ArrayList<String>> allItems = getItemsByColor();
        int totalChange = 0;
        for (String item : currPlayer.getItemNames()){
            if (allItems.get("bronze").contains(item)){ //NOSONAR
                totalChange += 5;
            } else if (allItems.get("silver").contains(item)){ //NOSONAR
                totalChange += 7;
            } else if (allItems.get("gold").contains(item)){ //NOSONAR
                totalChange += 10;
            }
        }
        currPlayer.addCash(totalChange);
        currPlayer.setItemNames(new ArrayList<>());
        toUsable(gameFlow, currPlayer);
        toCash(currPlayer, totalChange, gameFlow);
    }
    public void getMushroom(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Player[] currPlayers = gameFlow.getPlayers();
        int x = randomInt(2);
        if (x==0){
            currPlayer.addItemNames("MagicMushroom");//NOSONAR
            toUsable(gameFlow, currPlayer);
            return;
        } else{
            for (Player p : currPlayers){
                if (!p.equals(currPlayer)){
                    p.addItemNames("MagicMushroom");
                }
            }
        }
        toUsable(gameFlow);
    }
    public void mustBuyItemOrCard(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        int cash = Math.min(15, currPlayer.getCash());
        if (cash>=15){
            String it = GameFlow.randoItem();
            currPlayer.addItemNames(it);
        } else {
            String itt = GameFlow.randoCard();
            currPlayer.addCardNames(itt);
        }
        currPlayer.addCash(-cash);
        toUsable(gameFlow, currPlayer);
        toCash(currPlayer, -cash, gameFlow);
    }
    public void stealOthersMoney(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Player[] currPlayers = gameFlow.getPlayers();
        int cash = 0;
        CashData cashData = new CashData(gameFlow);
        for (Player p : currPlayers){
            if (!p.equals(currPlayer)){
                int maxi = Math.min(10, p.getCash());
                cash += maxi;
                p.addCash(-maxi);
                p.addLostCash(maxi);
                cashData.setPlayerAmountAndUpdate(p.getPlayerId().intValue(), p.getCash(), -maxi);
            }
        }
        currPlayer.addCash(+cash);
        cashData.setPlayerAmountAndUpdate(currPlayer.getPlayerId().intValue(), currPlayer.getCash(), cash);
        GameWebSocketController.returnMoney(cashData, gameFlow.getGameId());
    }
    public void nothing(GameFlow gameFlow) {
        //nothing, next player
    }
    public void found20Money(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Player[] currPlayers = gameFlow.getPlayers();
        int x = randomInt(3);
        int mateId = currPlayer.getTeammateId().intValue();
        if (x==0){
            currPlayer.addCash(+20);
            toCash(currPlayer, 20, gameFlow);
        }else if (x==1) {
            currPlayers[mateId - 1].addCash(+20);
            toCash(currPlayers[mateId-1], 20, gameFlow);
        }
    }
    public void teleportToRandom(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Player[] currPlayers = gameFlow.getPlayers();
        int x = randomInt(2);
        int rando = randomInt(currPlayers.length);
        if (currPlayers[rando]==currPlayer){
            rando = (rando + 1 >= currPlayers.length) ? 1 : rando+1;
        }
        Long pPosi = currPlayer.getPosition();
        Long rPosi = currPlayers[rando].getPosition();
        currPlayer.setPosition(rPosi);
        if (x==0){
            currPlayers[rando].setPosition(pPosi);
            toMoveTp(currPlayer, currPlayers[rando], gameFlow);
        }else{
            toMoveTp(currPlayer, gameFlow);
        }
    }
    public void getRandomStuff(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Player[] currPlayers = gameFlow.getPlayers();
        int x = randomInt(3);
        if (x==0){
            int mateId = currPlayer.getTeammateId().intValue();
            currPlayer.addItemNames("TheBrotherAndCo"); //NOSONAR
            currPlayers[mateId-1].addItemNames("TheBrotherAndCo");
            toUsable(gameFlow, currPlayer);
            toUsable(gameFlow, currPlayers[mateId-1]);
        } else if (x==1){
            int rando = randomInt(GameFlow.allCards.length);
            currPlayer.addCardNames(GameFlow.allCards[rando]);
            int randi = randomInt(GameFlow.allCards.length);
            currPlayer.addCardNames(GameFlow.allCards[randi]);
            toUsable(gameFlow, currPlayer);
        } else {
            ArrayList<String> goldItems = getItemsByColor().get("gold");
            int y = randomInt(goldItems.size());
            String ite = goldItems.get(y);
            currPlayer.addItemNames(ite);
            toUsable(gameFlow, currPlayer);
        }
    }
    public void gift10Money(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Player[] currPlayers = gameFlow.getPlayers();
        int mateId = currPlayer.getTeammateId().intValue();
        int cash = Math.min(10, currPlayer.getCash());
        currPlayer.addCash(-cash);
        currPlayers[mateId-1].addCash(+cash);
        toCash(currPlayer, currPlayers[mateId-1], -cash, gameFlow);
    }
    public void sellAllCards(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Map<String, ArrayList<String>> allCards = getCardsByColor();
        int allCash = 0;
        for (String card : currPlayer.getCardNames()){
            if (allCards.get("bronze").contains(card)){
                allCash += 5;
            } else if (allCards.get("silver").contains(card)){
                allCash += 7;
            } else if (allCards.get("gold").contains(card)){
                allCash += 10;
            }
        }
        currPlayer.addCash(allCash);
        currPlayer.setCardNames(new ArrayList<>());

        toUsable(gameFlow, currPlayer);
        toCash(currPlayer, allCash, gameFlow);
    }
    public void getOthersCards(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Player[] currPlayers = gameFlow.getPlayers();
        CashData cashData = new CashData(gameFlow);
        for (Player p : currPlayers){
            if (p.getCardNames().isEmpty()){
                int cash = Math.min(5, p.getCash());
                p.addCash(-cash);
                cashData.setPlayerAmountAndUpdate(p.getPlayerId().intValue(), p.getCash(), -cash);
            } else {
                String cardname = p.getCardNames().get(0);
                currPlayer.addCardNames(cardname);
                p.removeCardNames(cardname);
            }
        }
        GameWebSocketController.returnMoney(cashData, gameFlow.getGameId());
        toUsable(gameFlow);
    }
    public void surpriseMF(GameFlow gameFlow) {
        catnami(gameFlow);
    }
    public void swapCardsOrItems(GameFlow gameFlow) {
        Player currPlayer = gameFlow.getPlayers()[gameFlow.getTurnPlayerId().intValue()-1];
        Player[] currPlayers = gameFlow.getPlayers();
        int x = randomInt(2);
        int mateId = currPlayer.getTeammateId().intValue();
        if (x==0){
            ArrayList<String> temp1 = currPlayer.getItemNames();
            currPlayer.setItemNames(currPlayers[mateId-1].getItemNames());
            currPlayers[mateId-1].setItemNames(temp1);
        }else{
            ArrayList<String> temp2 = currPlayer.getCardNames();
            currPlayer.setCardNames(currPlayers[mateId-1].getCardNames());
            currPlayers[mateId-1].setCardNames(temp2);
        }
        toUsable(gameFlow);
    }


    /**
     *
     * ITEM COLOR
     * CARD COLOR
     *
     */
    private Map<String, ArrayList<String>> getItemsByColor(){
        ArrayList<String> bronze = new ArrayList<>(Arrays.asList("MagicMushroom", "TheBrotherAndCo", "PeaceImOut", "WhatsThis", "IceCreamChest")); // , "TwoMushrooms"
        ArrayList<String> silver = new ArrayList<>(Arrays.asList("SuperMagicMushroom", "TreasureChest", "ImOut", "MeowYou", "XBoxController")); // , "Stick", "BadWifi"
        ArrayList<String> goldes = new ArrayList<>(Arrays.asList("UltraMagicMushroom", "OnlyFansSub", "BestTradeDeal", "ItemsAreBelongToMe")); // , "Confusion", "GoldenSnitch", "ChickyNuggie"
        return Map.ofEntries(Map.entry("bronze", bronze), Map.entry("silver", silver), Map.entry("gold", goldes));
    }
    private Map<String, ArrayList<String>> getCardsByColor(){
        ArrayList<String> bronze = new ArrayList<>(Arrays.asList("B14", "B26", "B35", "B135", "B246", "B123", "B456", "B07"));
        ArrayList<String> silver = new ArrayList<>(Arrays.asList("S0", "S1", "S2", "S3", "S4", "S5", "S6", "S7"));
        ArrayList<String> goldes = new ArrayList<>(Arrays.asList("G13", "G26", "G45", "G04", "G37", "G1256"));
        return Map.ofEntries(Map.entry("bronze", bronze), Map.entry("silver", silver), Map.entry("gold", goldes));
    }


    /**
     *
     * PREPARE MESSAGES FOR WEBSOCKETS
     *
     */
    private void toWinCondi(Player p1, Player p2, GameFlow gameFlow){
        gameFlow.checkWinCondition(p1);
        gameFlow.checkWinCondition(p2);
    }

    private void toUsable(GameFlow gameFlow, Player player){
        Map<String, Object> retour = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        response.put("items", player.getItemNames());//NOSONAR
        response.put("cards", player.getCardNames());//NOSONAR
        retour.put(player.getPlayerId().toString(), response);
        GameWebSocketController.returnUsables(retour, gameFlow.getGameId());
    }

    private void toUsable(GameFlow gameFlow){
        UsableData usableData = UsableData.prepateData(gameFlow);
        GameWebSocketController.returnUsables(usableData, gameFlow.getGameId());
    }

    private void toCash(Player player, int change, GameFlow gameFlow){
        if (change<0){
            player.addLostCash(-change);
        }
        CashData cashData = new CashData(gameFlow);
        cashData.setPlayerAmountAndUpdate(player.getPlayerId().intValue(), player.getCash(), change);
        GameWebSocketController.returnMoney(cashData, gameFlow.getGameId());
    }

    private void toCash(Player player1, Player player2, int change, GameFlow gameFlow){
        if (change<0){
            player1.addLostCash(-change);
        }
        CashData cashData = new CashData(gameFlow);
        cashData.setPlayerAmountAndUpdate(player1.getPlayerId().intValue(), player1.getCash(), change);
        cashData.setPlayerAmountAndUpdate(player2.getPlayerId().intValue(), player2.getCash(), -change);

        GameWebSocketController.returnMoney(cashData, gameFlow.getGameId());
    }

    private void toMoveTp(Player player, GameFlow gameFlow){
        MoveData moveData = new MoveData("teleport");
        ArrayList<Long> posiArr = new ArrayList<>(Arrays.asList(player.getPosition()));
        moveData.setPlayerSpaceMovesColour(player.getPlayerId().intValue(), posiArr, 0, null);
        Map<String, Object> oneMoveData = moveData.getPlayerMoveMap(player.getPlayerId().intValue());
        GameWebSocketController.returnMoves(oneMoveData, gameFlow.getGameId());
    }

    private void toMoveTp(Player player1, Player player2, GameFlow gameFlow){
        MoveData moveData = new MoveData("teleport");
        ArrayList<Long> posiArr1 = new ArrayList<>(Arrays.asList(player1.getPosition()));
        ArrayList<Long> posiArr2 = new ArrayList<>(Arrays.asList(player2.getPosition()));
        moveData.setPlayerSpaceMovesColour(player1.getPlayerId().intValue(), posiArr1, 0, null);
        moveData.setPlayerSpaceMovesColour(player2.getPlayerId().intValue(), posiArr2, 0, null);
        Map<String, Object> twoMoveData = moveData.getPlayerMoveMap(player1.getPlayerId().intValue(), player2.getPlayerId().intValue());
        GameWebSocketController.returnMoves(twoMoveData, gameFlow.getGameId());
    }

    private void toMoveTp(Player player1, Player player2, Player player3, Player player4, GameFlow gameFlow){
        MoveData moveData = new MoveData("teleport");
        ArrayList<Long> posiArr1 = new ArrayList<>(Arrays.asList(player1.getPosition()));
        ArrayList<Long> posiArr2 = new ArrayList<>(Arrays.asList(player2.getPosition()));
        ArrayList<Long> posiArr3 = new ArrayList<>(Arrays.asList(player3.getPosition()));
        ArrayList<Long> posiArr4 = new ArrayList<>(Arrays.asList(player4.getPosition()));
        moveData.setPlayerSpaceMovesColour(player1.getPlayerId().intValue(), posiArr1, 0, null);
        moveData.setPlayerSpaceMovesColour(player2.getPlayerId().intValue(), posiArr2, 0, null);
        moveData.setPlayerSpaceMovesColour(player3.getPlayerId().intValue(), posiArr3, 0, null);
        moveData.setPlayerSpaceMovesColour(player4.getPlayerId().intValue(), posiArr4, 0, null);
        Map<String, Object> twoMoveData = moveData.getPlayerMoveMap(player1.getPlayerId().intValue(), player2.getPlayerId().intValue(), player3.getPlayerId().intValue(), player4.getPlayerId().intValue());
        GameWebSocketController.returnMoves(twoMoveData, gameFlow.getGameId());
    }
}
