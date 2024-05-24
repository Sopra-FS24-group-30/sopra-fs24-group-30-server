package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Effects.Getem;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.*;
import ch.uzh.ifi.hase.soprafs24.service.AchievementService;
import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;
import org.json.JSONObject;
import org.json.*;
import java.util.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController.*;


public class GameFlow {

    private final Spaces spaces = new Spaces();

    protected static final String[] allItems = Getem.getItems().keySet().toArray(new String[0]);
    protected static final String[] allCards = Getem.getCards().keySet().toArray(new String[0]);

    private Long gameId;
    private Player[] players = new Player[4];
    private GameBoard gameBoard;
    private Long turnPlayerId;
    private int currentTurn;
    private int turnCounter;
    private int movesLeft;
    private boolean hadJunction = false;
    private boolean hadJunctionForGoal = false;
    private boolean itemultused;
    private boolean cardDiceUsed;
    private LocalDateTime startTime;
    private Map<String, Object> winMsg;
    private JSONObject choices;

    public Player getActivePlayer(){
        return this.players[this.turnPlayerId.intValue()-1];
    }

    public boolean isItemultused() {
        return itemultused;
    }

    public void setItemultused(boolean itemultused) {
        this.itemultused = itemultused;
    }

    public boolean isCardDiceUsed() {
        return cardDiceUsed;
    }

    public void setCardDiceUsed(boolean cardDiceUsed) {
        this.cardDiceUsed = cardDiceUsed;
    }

    public JSONObject getChoices() {
        return choices;
    }

    public void setChoices(JSONObject choices) {
        this.choices = choices;
    }

    public Long getGameId() {
        return gameId;
    }
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Player[] getPlayers() {
        return players;
    }
    public Player getPlayer(Integer playerId) {
        return players[playerId - 1];
    }

    public GameBoard getGameBoard(){
        return gameBoard;
    }

    public void setGameBoard() {
        this.gameBoard = new GameBoard();
    }

    public Long getTurnPlayerId() {
        return turnPlayerId;
    }
    public void setTurnPlayerId(Long turnoPlayerId) {
        this.turnPlayerId = turnoPlayerId;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }
    public void setCurrentTurn(int currTurn) {
        this.currentTurn = currTurn;

    }

    public void setMovesLeft(int movesLeft) {
        this.movesLeft = movesLeft;
    }
    public int getMovesLeft() {
        return movesLeft;
    }

    public void setHadJunction(boolean hadJunction) {
        this.hadJunction = hadJunction;
    }
    public boolean getHadJunction(){
        return hadJunction;
    }

    public void setHadJunctionForGoal(boolean hadJunctionForGoal) {
        this.hadJunctionForGoal = hadJunctionForGoal;
    }
    public boolean getHadJunctionForGoal(){
        return hadJunctionForGoal;
    }

    public int getTurnCounter() {
        return turnCounter;
    }
    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Map<String, Object> getWinMsg() {
        return winMsg;
    }
    public void setWinMsg(Map<String, Object> winMsg) {
        this.winMsg = winMsg;
    }

    public GameFlow(){
        //this is needed for tests and creating a GameFlow
    }

    /*
    possible Effects
    Items can have the following effects:
        give a player more turns
        update a players money
        let a player move differently (abiegekarte)
        get cards from other players
        give a player a special status in case he lands on a field or overtakes other people
        teleport players
        exchange cards/items from players (can steal with nothing given back)
        exchange something for usables (if getting give nothing back)
        mute a player
        force a player to do an action
     */


    /**
     * find the starting fields for the players
     * @param playerId
     * @return left or right starting field Id
     */
    public static Long findStart(Integer playerId){
        switch (playerId){
            case 1, 2:
                return 53L;
            case 3, 4:
                return 54L;
            default:
                return 0L;
        }
    }



    /**
     * resolve the special fieldId to an actual ID
     * @param fieldId special fieldId
     * @param playerId player for whom the field neeeds to be found
     * @return actual field ID
     */
    private Long getField(String fieldId, Integer playerId){
        switch (fieldId){
            case "start":
                return findStart(playerId);
            case "choice": //NOSONAR
                return Long.valueOf(choices.getString("field"));//NOSONAR
            case "randomPlayer":
                int player;
                do{
                    player = (int) (Math.random() * 3 + 1); //NOSONAR
                }while (player == turnPlayerId);
                return players[player-1].getPosition();
            case "random"://NOSONAR
                return (long) (int) (Math.random()*52+1); //NOSONAR
            default:
                return (long) Integer.parseInt(fieldId);
        }
    }

    public void updateTurns(JSONObject args){
        int turn = args.getInt("newTurnNumber");
        setCurrentTurn(turn);
        TurnActiveData turnActiveData = TurnActiveData.prepareData(this);
        GameWebSocketController.returnTurnActive(turnActiveData,gameId);
    }

    public void useRandomUsable(JSONObject args){
        String type = args.getString("type");
        Integer amount = args.getInt("amount"); //NOSONAR
        switch (type){ //NOSONAR
            case "item":
                for (int i=1;i<=amount;i++){
                    String itemName = Getem.getNoChoiceItem();
                    getPlayer(turnPlayerId.intValue()).addItemNames("itemName");
                    System.out.println("item USed: " + itemName);
                    this.setItemultused(false); //this is because this gets called by chameleon so it is already set to true but we need to run the item
                    GameWebSocketController.handleItems("{\"used\": \"" + itemName + "\", \"choice\": {}}",gameId);
                }
                break;
            default:
                throw new RuntimeException("the option " + type + " is not yet implemented"); //NOSONAR

        }

    }

    public void rechargeUlt(JSONObject args){
        String playerId = args.getString("player");//NOSONAR
        String cashPay = args.getString("cash");
        ArrayList<Integer> allPlayers = specialIds(playerId);

        for(Integer id : allPlayers){
            Player player = getPlayer(id);
            int hasToPay = Math.max(player.getCash()*-1,Integer.parseInt(cashPay));
            player.addCash(hasToPay);
            CashData cashData = new CashData();
            cashData.setupCashDataCurrent(this);
            cashData.setPlayerAmount(id,hasToPay);
            returnMoney(cashData,gameId);
            player.setUltActive(true);
            UltimateData ultimateData = new UltimateData();
            ultimateData.prepareDataForCurrentPlayer(this);
            returnUltToPlayer(ultimateData,gameId,player.getUserId());
        }
    }

    public void shuffle(JSONObject args){
        String type = args.getString("type");
        switch (type){ //NOSONAR
            case "ultimates":
                ArrayList<String> winConditions = new ArrayList<>();
                for(Player player : players){
                    winConditions.add(player.getWinCondition());
                }
                Collections.shuffle(winConditions);
                for(int i=0;i<4;i++){
                    players[i].setWinCondition(winConditions.get(i));
                    checkWinCondition(players[i]);
                }
                break;
            default:
                throw new RuntimeException("this type of shuffling: " + type + " is not yet implemented"); //NOSONAR

        }
    }

    /**
     * teleport players to a field
     * @param args parameters of the updatepositions effect
     * @return key: playerId, value: the new fieldId where the player gets teleported to
     */

    public void givePlayerCardRand(JSONObject args) {
        String[]  cards_in_game = Getem.getCards().keySet().toArray(new String[0]); //NOSONAR
        SecureRandom random = new SecureRandom();
        String randomCard = cards_in_game[random.nextInt(cards_in_game.length)];
        players[(int) (long) getTurnPlayerId()-1].addCardNames(randomCard);
        returnUsables(UsableData.prepateData(this),getGameId());

    }

    public void reduceMoneyALL(JSONObject args){ //NOSONAR
        for(int i = 0; i < 4; i++){
            players[i].setCash(players[i].getCash()-5);
        }
        CashData cashData = new CashData();
        cashData.setPlayersNewCash(players[0].getCash(),players[1].getCash(),players[2].getCash(),players[3].getCash());
        GameWebSocketController.returnMoney(cashData,gameId);

    }


    public void exchangePositions(JSONObject args){
        HashMap<Integer, ArrayList<Long>> updatedPositions = new HashMap<>();
        String playerSpecialId = args.getString("player"); //NOSONAR
        String fieldSpecialId = args.getString("field");//NOSONAR
        int currentPlayerIndex = (int) (long) getTurnPlayerId() - 1;
        Player currentPlayer = players[currentPlayerIndex];//NOSONAR
        SecureRandom random = new SecureRandom();
        int randomPLayerIndex;
        // Get current player and random player
        do {
            randomPLayerIndex = random.nextInt(players.length);
        } while (randomPLayerIndex == currentPlayerIndex);
        Player randomPlayer = players[randomPLayerIndex];

        // Store initial positions of the players
        Long currentPosition = getActivePlayer().getPosition();
        Long randomPosition = randomPlayer.getPosition();

        // Swap positions of the players
        players[currentPlayerIndex].setPosition(randomPosition);
        randomPlayer.setPosition(currentPosition);

        // Update positions map
        ArrayList<Long> currentPlayerPositionList = new ArrayList<>();
        currentPlayerPositionList.add(players[currentPlayerIndex].getPosition());
        updatedPositions.put(currentPlayerIndex, currentPlayerPositionList);

        ArrayList<Long> randomFieldIds = new ArrayList<>();
        randomFieldIds.add(players[randomPLayerIndex].getPosition());
        updatedPositions.put(randomPLayerIndex, randomFieldIds);

        MoveData moveData = new MoveData(updatedPositions.get(1),updatedPositions.get(2),updatedPositions.get(3),updatedPositions.get(4));

        GameWebSocketController.returnMoves(moveData,gameId);
    }
    public void updatePositions(JSONObject args){
        HashMap<Integer, ArrayList<Long>> updatedPositions = new HashMap<>();
        String playerSpecialId = args.getString("player");//NOSONAR
        String fieldSpecialId = args.getString("field");
        ArrayList<Integer> playersToUpdate = new ArrayList<>(specialIds(playerSpecialId));


        for(Integer player : playersToUpdate){
            ArrayList<Long> fieldIds = new ArrayList<>();
            fieldIds.add(getField(fieldSpecialId,player));
            updatedPositions.put(player,fieldIds);
            players[player-1].setPosition(fieldIds.get(0));
            MoveData moveData1 = new MoveData();
            moveData1.setPlayerSpaceMovesColour(player,fieldIds,0,null);
            GameWebSocketController.returnMoves(moveData1.getPlayerMoveMap(player),gameId);
        }
    }


    //TODO: add support for giving items to multiple people as of now can only exchange with one
    //TODO: get the itemNames to be exchanged

    /**
     * exchange usables between players
     */

    public void exchangeAll(){
        String playerId = getChoices().getString("playerId");//NOSONAR
        Player otherplayey = players[Integer.parseInt(playerId)-1];
        Player currentPlayer = players[(int) (long) getTurnPlayerId()-1];
        ArrayList<String> otherPlayerItems = otherplayey.getItemNames();
        for (String item : otherPlayerItems){
            currentPlayer.addItemNames(item);
        }
        ArrayList<String> currentPlayerCards = currentPlayer.getCardNames();
        for (String card : currentPlayerCards){
            otherplayey.addCardNames(card);
        }
        otherplayey.setItemNames(new ArrayList<>());
        currentPlayer.setCardNames(new ArrayList<>());

        UsableData usableData = new UsableData();
        usableData.setItems(players[0].getItemNames(),players[1].getItemNames(),players[2].getItemNames(),players[3].getItemNames());
        usableData.setCards(players[0].getCardNames(),players[1].getCardNames(),players[2].getCardNames(),players[3].getCardNames());
        GameWebSocketController.returnUsables(usableData,gameId);


    }
    public void exchange(JSONObject args){
        JSONObject giveInfos = args.getJSONObject("give");
        JSONObject getInfos = args.getJSONObject("get");

        ArrayList<String> giveUsables = new ArrayList<>();
        ArrayList<String> getUsables = new ArrayList<>();

        ArrayList<Integer> givePlayers = specialIds(giveInfos.getString("player"));
        String giveType = giveInfos.getString("type");
        String giveSelection = giveInfos.getString("selection");
        Integer giveAmount = giveInfos.getInt("amount");//NOSONAR

        ArrayList<Integer> getPlayers = specialIds(getInfos.getString("player"));
        String getType = getInfos.getString("type");
        String getSelection = getInfos.getString("selection");
        Integer getAmount = getInfos.getInt("amount");

        if (giveAmount == 100){
            giveAmount = players[(int) (long) getTurnPlayerId()-1].getCardNames().size();
        }

        if (getAmount == 100){
            getAmount = players[getPlayers.get(0) -1].getItemNames().size();
        }

        giveUsables.addAll(getType(givePlayers, giveType, giveSelection, giveAmount));
        getUsables.addAll(getType(getPlayers, getType, getSelection, getAmount));


        for(int playerId : givePlayers){
            System.out.println("giving player: " + playerId);
            updateUsables(playerId,getUsables,getType);
        }
        for(int playerId : getPlayers){
            System.out.println("getting player " + playerId);
            updateUsables(playerId,giveUsables,giveType);
        }

        UsableData usableData = UsableData.prepateData(this);
        GameWebSocketController.returnUsables(usableData,gameId);
    }

    /**
     * add the usables to the respective player
     * @param playerId
     * @param usables usables which the player should get
     * @param type item or cards
     */
    private void updateUsables(int playerId, ArrayList<String> usables, String type){
        switch (type){ //NOSONAR
            case "item":
                for (String u : usables){
                    players[playerId-1].addItemNames(u);
                }
                break;
            case "card":
                for (String u : usables){
                    players[playerId-1].addCardNames(u);
                }
                break;
        }
    }

    /**
     * get the list with usables based wheter item or card is specified and remove them from the player
     * @param exchangePlayers players for which usables need to be fetched
     * @param type item or card
     * @param selection all, random, choice
     * @param amount in case of choice or random, how many usables are to be fetched
     * @return gives back the usables
     */
    private ArrayList<String> getType(ArrayList<Integer> exchangePlayers, String type, String selection, Integer amount) {
        if(type != null){
            switch (type){ //NOSONAR
                case "item":
                    for(Integer player : exchangePlayers){
                        return getSelectedItems(selection,player,amount); //NOSONAR
                    }
                    break;
                case "card":
                    for(Integer player : exchangePlayers){
                        return (getSelectedCards(selection,player,amount)); //NOSONAR
                    }
            }
        }
        return new ArrayList<>();
    }


    //TODO: give infos to frontend what was removed

    /**
     * remove the items from the player and give the items gained this way back in a list
     * @param selection type of selections
     * @param playerid ID of the concerning player
     * @param amount how many items are given
     * @return all the items which are ready for exchange
     */
    private ArrayList<String> getSelectedItems(String selection, int playerid, Integer amount){
        ArrayList<String> returnItems = new ArrayList<>();
        if(selection == null){
            return returnItems;
        }
        ArrayList<String> playerItems = players[playerid-1].getItemNames();
        switch(selection){ //NOSONAR
            case "random":
                int limit = Math.min(amount,playerItems.size());
                for(int i = 0; i<limit;i++){
                    int select = (int) (Math.random()*playerItems.size()); //NOSONAR
                    String itemName = playerItems.get(select);
                    returnItems.add(itemName);
                    players[playerid-1].removeItemNames(itemName);
                }
                break;
            case "all":
                returnItems.addAll(playerItems);
                players[playerid-1].setItemNames(new ArrayList<>());
                break;
            case "choice":
                JSONArray choiceItems = choices.getJSONArray("items");
                ArrayList<String> itemNames = new ArrayList<>();
                int len = choiceItems.length();
                for(int i=0;i<len;i++){
                    itemNames.add(choiceItems.get(i).toString());
                }
                returnItems.addAll(itemNames);
                players[playerid-1].removeItemNames(itemNames);
                break;
        }

        return returnItems;
    }

    /**
     * remove the cards from the player and give the cards gained this way back in a list
     * @param selection type of selections
     * @param playerid ID of the concerning player
     * @param amount how many cards are given
     * @return all the items which are ready for exchange
     */
    private ArrayList<String> getSelectedCards(String selection, int playerid, Integer amount){
        ArrayList<String> returnCards = new ArrayList<>();
        if(selection == null){
            return returnCards;
        }
        ArrayList<String> playerCards = players[playerid-1].getCardNames();
        switch(selection){ //NOSONAR
            case "random":
                for(int i = 0; i<amount;i++){
                    int select = (int) (Math.random()*playerCards.size()); //NOSONAR
                    returnCards.add(playerCards.get(select));
                    playerCards.remove(select);
                }
                break;
            case "all":
                returnCards.addAll(playerCards);
                players[playerid-1].setCardNames(new ArrayList<>());
                break;
            case "choice":
                JSONArray choiceCards = choices.getJSONArray("cards");
                ArrayList<String> cardNames = new ArrayList<>();
                int len = choiceCards.length();
                for(int i=0;i<len;i++){
                    cardNames.add(choiceCards.get(i).toString());
                }
                returnCards.addAll(cardNames);
                players[playerid-1].removeCardNames(cardNames);
                break;
        }
        return returnCards;
    }

    /**
     * update the money of players
     * @param args parameters for the updatemoney effect
     * @return key: playerId, value: the new amount of money the player has
     */
    public void updateMoney(JSONObject args){
        String type = args.getString("type");
        Hashtable<Long,Integer> playersPayMoney; //NOSONAR

        playersPayMoney = effectivePayAmounts(args.getJSONObject("amount"),type);

        CashData cashData = new CashData(this);
        cashData.setPlayersNewCash(players[0].getCash(),players[1].getCash(),players[2].getCash(),players[3].getCash());
        for(Long key : playersPayMoney.keySet()){ //NOSONAR
            cashData.setPlayerAmount(key.intValue(),playersPayMoney.get(key));
        }
        HashMap<Long,Integer> hashi = cashData.checkNegativeChanges();
        for (Long key : hashi.keySet()){ //NOSONAR
            getPlayer(key.intValue()).addLostCash(-hashi.get(key));
        }
        GameWebSocketController.returnMoney(cashData,gameId);
    }

    public Map<String, Object> updateCardPositions (JSONObject args, int count){
        JSONArray movesArray = args.getJSONArray("moves");
        String category = args.getString("category");
        switch (category){ //NOSONAR
            case "Silver":
                int moves = movesArray.getInt(0);
                Long playerId = getTurnPlayerId();
                System.out.println(playerId);
                System.out.println(moves);
                move(moves, players[(int) (long) playerId-1].getPosition());
                break;
            case "Bronze":
                int movesArrayLength = movesArray.length();
                SecureRandom random = new SecureRandom();
                int randomIndex = random.nextInt(movesArrayLength);
                int randomMoves = movesArray.getInt(randomIndex);
                Long randomPlayerId = getTurnPlayerId();
                move(randomMoves, players[(int) (long) randomPlayerId-1].getPosition());
                break;
            case "Gold":
                move(count, players[(int) (long) getTurnPlayerId()-1].getPosition());
                break;
            default:
                throw new RuntimeException("the card with type " + category + " does not exist"); //NOSONAR
        }

        return Collections.emptyMap();
    }

    /**
     * give in how much each player should pay in order to get how much they will pay based on how much cash they have
     * @param amounts the parameters to be processed
     * @return PlayerIds,Amount
     */

    private Hashtable<Long,Integer> effectivePayAmounts(JSONObject amounts, String type){//NOSONAR
        int totalPot = 0;
        ArrayList<Integer> potWinners = new ArrayList<>();
        Hashtable<Long,Integer> calculatedAmount = new Hashtable<>(); //NOSONAR
        Iterator<String> keys = amounts.keys();
        while(keys.hasNext()){
            String key = keys.next();
            ArrayList<Integer> playerIds = specialIds(key);
            for (Integer id : playerIds) {
                Integer amount = moneyDescToNumber(amounts.getString(key),id);
                if (amount == null){
                    potWinners.add(id);
                    calculatedAmount.put(Long.valueOf(id),0);
                }else if(amount < 0){
                    switch (type){//NOSONAR
                        case "absolute":
                            int toPayAbsolute = getMaxPay(players[id-1].getPlayerId().intValue(),amount);
                            totalPot += toPayAbsolute;
                            players[id-1].setCash(players[id-1].getCash()+toPayAbsolute);
                            calculatedAmount.put(Long.valueOf(id),amount);
                            break;
                        case "relative":
                            int toPayRelative = getMaxPay(id,(int) (players[id-1].getCash() / 100.0 * amount));
                            totalPot += toPayRelative;
                            players[id-1].setCash(players[id-1].getCash()+toPayRelative);
                            calculatedAmount.put(Long.valueOf(id),toPayRelative);
                            break;
                    }
                }else{
                    switch (type){//NOSONAR
                        case "absolute":
                            players[id-1].setCash(players[id-1].getCash()+amount);
                            calculatedAmount.put(Long.valueOf(id),amount);
                            break;
                        case "relative":
                            int toPayRelative = (int) (players[id-1].getCash() / 100.0 * amount);
                            getPlayer(id).setCash(getPlayer(id).getCash()+toPayRelative);
                            calculatedAmount.put(Long.valueOf(id),toPayRelative);
                            break;
                    }
                }
            }
        }
        totalPot = totalPot * -1;
        for(Integer id : potWinners){
            int changeMoney = totalPot/potWinners.size();
            players[id-1].setCash(players[id-1].getCash()+changeMoney);
            calculatedAmount.put(Long.valueOf(id),changeMoney);
        }

        return calculatedAmount;
    }

    /**
     * helperfunction for calculating how much money is given
     * @param description the defined amount
     * @return the actual value, 1000 used for max
     */
    private Integer moneyDescToNumber(String description, int playerId){
        if(description.equals("givenAmount")){
            return null;
        }
        else if (description.equals("everything")) {
            return players[playerId-1].getCash()*-1;
        }
        else{
            return Integer.valueOf(description);
        }
    }

    /**
     * convert the specialIds to actual ids
     * @param specialId give in the special Id
     * @return return the id(s) in an arraylist
     */

    private ArrayList<Integer> specialIds(String specialId){

        ArrayList<Integer> playerIds = new ArrayList<>();

        switch (specialId){
            case "current":
                playerIds.add((int) (long) turnPlayerId);
                break;
            case "others":
                for(int i=1;i<=4;i++){
                    if(i != (int) (long) turnPlayerId){
                        playerIds.add(i);
                    }
                }
                break;
            case "all":
                for(int i=1;i<=4;i++){
                    playerIds.add(i);
                }
                break;
            case "teammate":
                int mate = (int) (long) turnPlayerId+2;
                if(mate > 4){
                    mate = mate % 4;
                }
                playerIds.add(mate);
                break;
            case "enemy":
                int current = (int) (long) turnPlayerId;
                if(current % 2 == 0){
                    playerIds.add(1);
                    playerIds.add(3);
                }else{
                    playerIds.add(2);
                    playerIds.add(4);
                }
                break;
            case "choice":
                playerIds.add(Integer.valueOf(choices.getString("playerId")));
                break;
            default:
                playerIds.add(Integer.valueOf(specialId));
        }
        return playerIds;
    }

    /**
     * throws multiple dice and gives money if the numbers match, returns the summed number which was thrown
     * @param definition parameters for givePlayerDiceEffect
     * @return the dice throws
     */
    //TODO: call move with total
    public void givePlayerDice(JSONObject definition){

        int diceCount = definition.getInt("dice");
        int bonusCount = definition.getInt("bonusCount");
        int cashAmount = definition.getInt("money");
        ArrayList<Integer> diceThrows = new ArrayList<>();

        diceThrows.addAll(throwDice(diceCount));

        for(int diceValue=1;diceValue<=6;diceValue++){
            if(Collections.frequency(diceThrows,diceValue) == bonusCount){
                players[turnPlayerId.intValue()-1].addCash(cashAmount);
                CashData cashData = new CashData(this);
                cashData.setPlayerAmount(turnPlayerId.intValue(),cashAmount);
                GameWebSocketController.returnMoney(cashData,gameId);
            }
        }
        DiceData diceData = new DiceData(diceThrows);
        GameWebSocketController.returnDice(diceData,gameId);
        int totalAmount = 0;
        for(Integer dice : diceThrows){
            totalAmount += dice;
        }
        this.setCardDiceUsed(true);
        move(totalAmount,getPlayer(turnPlayerId.intValue()).getPosition());
    }

    public ArrayList<Integer> throwDice(int amount){ //NOSONAR
        ArrayList<Integer> diceThrows = new ArrayList<>();
        for(int i=0;i<amount;i++){
            diceThrows.add((int) (Math.random()*6+1)); //NOSONAR
        }
        return diceThrows;
    }

    /**
     * check if the player can pay if not reduce the amount to the maximum payable
     * @param playerId id of the player
     * @param cashAmount cash amount if has to pay negative amount
     * @return amount the player can pay
     */
    private int getMaxPay(int playerId, int cashAmount){
        return (Math.max(cashAmount,getPlayer(playerId).getCash()*-1));
    }

    public void addPlayer(Player player){
        players[(int) (long)player.getPlayerId()-1] = player;
    }

    public static String randoItem(){
        return allItems[(int) (Math.random()*allItems.length)]; //NOSONAR
    }

    public static String randoCard(){
        return allCards[(int) (Math.random()*allCards.length)]; //NOSONAR
    }

    public List<Long> findMostCash(Player[] players){
        List<Long> richest = new ArrayList<>();
        int maxCash = players[0].getCash();
        for (Player player : players){
            if (player.getCash() == maxCash){
                richest.add(player.getPlayerId());
            } else if (player.getCash() > maxCash){
                richest.clear();
                richest.add(player.getPlayerId());
                maxCash = player.getCash();
            }
        }
        return richest;
    }

    public Map<String, Object> nextPlayer() {
        turnPlayerId++;
        turnCounter++;
        if (turnPlayerId > 4L){
            turnPlayerId = 1L;
        }
        if (turnCounter%4 == 0) {
            currentTurn++;
        }
        Map<String, Object> retour = new HashMap<>();
        retour.put("currentTurn", currentTurn);
        retour.put("activePlayer", turnPlayerId.toString());
        return retour;
    }

    public Map<String, Object> move(int moves, long posi) { //NOSONAR
        Player player = players[(int) (turnPlayerId-1)];
        Long currPosi = posi;
        int movies = moves;
        List<GameBoardSpace> allSpaces = getGameBoard().getSpaces();

        maroonedCompanyShipUnlucky(player, moves);

        List<Long> listi = new ArrayList<>();

        GameBoardSpace currentSpace = findSpaceById(allSpaces, currPosi);
        List<String> nextSpaceIds = currentSpace.getNext(); //NOSONAR
        Long nextPosi = Long.parseLong(nextSpaceIds.get(0));
        GameBoardSpace nextSpace = findSpaceById(allSpaces, nextPosi);
        String color = currentSpace.getColor();

        if (getHadJunction()){
            movies--;
            listi.add(currPosi);
            player.setPosition(currPosi);
            setHadJunction(false);
            setHadJunctionForGoal(true);
        }

        //check if game is over, or player gets cash, in case the player moves 0
        if ((movies==0 || getHadJunctionForGoal()) && "BlueGoal".equals(color) && Boolean.TRUE.equals(currentSpace.getIsGoal())){
            return checkGoalGameOver(color, player, listi, movies, moves, allSpaces);
        }

        player.addLandedAll(currPosi);

        while (movies > 0) {
            setHadJunctionForGoal(false);
            currentSpace = findSpaceById(allSpaces, currPosi);
            nextSpaceIds = currentSpace.getNext(); //NOSONAR
            nextPosi = Long.parseLong(nextSpaceIds.get(0));
            listi.add(nextPosi);
            nextSpace = findSpaceById(allSpaces, nextPosi);
            color = nextSpace.getColor(); //NOSONAR

            currPosi = nextPosi;
            player.setPosition(currPosi);
            player.addLandedAll(currPosi);

            // check if game is over, or player gets cash, in usual case
            if ("BlueGoal".equals(color) && nextSpace.getIsGoal()) { //NOSONAR
                return checkGoalGameOver(color, player, listi, movies, moves, allSpaces);
            }

            // "partial end of walk", check on what decision can be done.
            // walk can never end in these space types btw (junction, gate, specialItem)
            if (nextSpace.getOnSpace() == null) {
                return switchOverSpace(player, nextSpace, movies, moves, color, currPosi, listi);
            }
            movies--;
        }

        GameWebSocketController.returnMoves(toMove(player, listi, moves, color), getGameId());

        if (moves == 0 || getHadJunctionForGoal()) {
            setHadJunctionForGoal(false);
            (spaces.runLandOns.get(currentSpace.getOnSpace())).apply(this); //NOSONAR
        } else{
            (spaces.runLandOns.get(nextSpace.getOnSpace())).apply(this); //NOSONAR
        }

        endOfWalkCheck(player, color, currentSpace);

        GameWebSocketController.newPlayer(nextPlayer(), getGameId());

        //check if Game is over
        if (currentTurn >= 21){
            GameWebSocketController.endGame(getGameId());
            setWinMsg(doGameOverMaxTurns(findMostCash(players)));
        }

        return Collections.emptyMap();
    }



    /**
     *
     * GameOver related functions
     *
     */
    public Map<String, Object> checkGoalGameOver(String color, Player player, List<Long> listi, int movies, int moves, List<GameBoardSpace> allSpaces){
        setMovesLeft(movies);
        if (player.getCanWin()) {
            GameWebSocketController.returnMoves(toMove(player, listi, moves, color), getGameId());
            // GAME OVER
            GameWebSocketController.endGame(getGameId());
            setWinMsg(doGameOverWinCondi(player));
            return Collections.emptyMap();
        }
        GameWebSocketController.returnMoves(toMove(player, listi, moves, color), getGameId());
        GameWebSocketController.returnMoney(toMoney(player, +15), getGameId());

        player.addPassGoal();

        checkWinCondition(player);

        GameWebSocketController.changeGoal(allSpaces, getGameId());
        if (movies <= 0){
            return Collections.emptyMap();
        }
        setHadJunctionForGoal(false);
        return move(getMovesLeft(), player.getPosition());
    }

    public Map<String, Object> doGameOverWinCondi(Player player){
        Map<String, Object> mappi = new HashMap<>();
        Set<String> winners = new HashSet<>();
        Set<String> winnersUsername = new HashSet<>();
        List<String> reason = new ArrayList<>();

        Long jack = 50L;

        for (Player p : players) {
            if (p.getWinCondition().equals("JackSparrow")) { //NOSONAR
                jack = p.getPlayerId();
                break;
            }
        }

        reason.add(player.getPlayerId().toString());
        reason.add(player.getWinCondition());
        winners.add(player.getPlayerId().toString());
        winnersUsername.add(player.getUser().getUsername());

        if (!player.getTeammateId().equals(jack)){
            winners.add(player.getTeammateId().toString());
            winnersUsername.add(getPlayer(player.getTeammateId().intValue()).getUser().getUsername());
            if (jack!=50L){
                winners.add(jack.toString());
                winnersUsername.add(getPlayer(jack.intValue()).getUser().getUsername());
            }
        }
        int sizeOfWinners = winners.size();
        for (Player play: players){
            play.getAchievementProgress().setWinnerAmount(sizeOfWinners);
        }


        String resWinner = winnersUsername.stream().map(String::valueOf).collect(Collectors.joining(","));
        String resReason = reason.stream().map(String::valueOf).collect(Collectors.joining(" has "));//NOSONAR
        mappi.put("winners", resWinner);//NOSONAR
        mappi.put("reason", resReason);//NOSONAR

        initializeUpdates(winners);
        return mappi;
    }

    public Map<String, Object> doGameOverMaxTurns(List<Long> rich) {
        Map<String, Object> mappi = new HashMap<>();
        Set<String> winners = new HashSet<>();
        Set<String> winnersUsername = new HashSet<>();
        List<String> reason = new ArrayList<>();

        for (Player player : players) {
            if (player.getWinCondition().equals("JackSparrow")) {
                Player currPlayer = players[player.getPlayerId().intValue() - 1];
                Player currPlayerMate = players[currPlayer.getTeammateId().intValue()];
                winners.add(players[player.getPlayerId().intValue() - 1].getTeammateId().toString());
                winnersUsername.add(currPlayerMate.getUser().getUsername());
                reason.add(currPlayer.getUser().getUsername());
                reason.add("JackSparrow");
                String resWinner = winnersUsername.stream().map(String::valueOf).collect(Collectors.joining(","));
                String resReason = reason.stream().map(String::valueOf).collect(Collectors.joining(" has "));
                mappi.put("winners", resWinner);
                mappi.put("reason", resReason);
                initializeUpdates(winners);
                return mappi;
            }
        }

        if (rich.size() == 1) {
            winners.add(rich.get(0).toString());
            winnersUsername.add(getPlayer(rich.get(0).intValue()).getUser().getUsername());
            winners.add(players[rich.get(0).intValue() - 1].getTeammateId().toString());
            winnersUsername.add(players[players[rich.get(0).intValue() - 1].getTeammateId().intValue()].getUser().getUsername());
            reason.add(getPlayer(rich.get(0).intValue()).getUser().getUsername());
            reason.add("maxCash");
            String resWinner = winnersUsername.stream().map(String::valueOf).collect(Collectors.joining(","));
            String resReason = reason.stream().map(String::valueOf).collect(Collectors.joining(" has "));
            mappi.put("winners", resWinner);
            mappi.put("reason", resReason);
            initializeUpdates(winners);
            return mappi;
        }

        int randNum = (int) (Math.random() * rich.size()); //NOSONAR
        winners.add(rich.get(randNum).toString());
        winnersUsername.add(getPlayer(rich.get(randNum).intValue()).getUser().getUsername());
        winners.add(players[rich.get(randNum).intValue() - 1].getTeammateId().toString());
        winnersUsername.add(players[players[rich.get(randNum).intValue() - 1].getTeammateId().intValue()-1].getUser().getUsername());
        reason.add(getPlayer(rich.get(randNum).intValue()).getUser().getUsername());
        reason.add("maxCash");

        int sizeOfWinners = winners.size();
        for (Player player: players){
            player.getAchievementProgress().setWinnerAmount(sizeOfWinners);
        }

        String resWinner = winnersUsername.stream().map(String::valueOf).collect(Collectors.joining(","));
        String resReason = reason.stream().map(String::valueOf).collect(Collectors.joining(" has "));
        mappi.put("winners", resWinner);
        mappi.put("reason", resReason);
        initializeUpdates(winners);
        return mappi;
    }

    public void initializeUpdates(Set<String> winners){
        LocalDateTime endTime = LocalDateTime.now();
        long timeDifferenceSeconds = this.startTime.until(endTime, ChronoUnit.SECONDS);
        for(String winner : winners){
            Player player = getPlayer(Integer.valueOf(winner));
            player.getAchievementProgress().setWinner(true);
            if(winners.contains(player.getTeammateId().toString())){
                player.getAchievementProgress().setTeamMateWinner(true);
            }
            player.getAchievementProgress().setCashWhenWinning(player.getCash());
        }
        for(Player player : players){
            player.getAchievementProgress().setElapsedSeconds(timeDifferenceSeconds);
            GetBean.getAchievementService().updateAchievements(player.getAchievementProgress());
        }
    }



    /**
     *
     * Helper for Board / End related things
     *
     */
    private GameBoardSpace findSpaceById(List<GameBoardSpace> spaces, Long spaceId) {
        for (GameBoardSpace space : spaces) {
            if (space.getSpaceId().equals(spaceId)) {
                return space;
            }
        }
        return null;
    }

    public Long findGoal(List<GameBoardSpace> spaces){
        for (GameBoardSpace space : spaces){
            if (Boolean.TRUE.equals(space.getIsGoal())){
                return space.getSpaceId();
            }
        }
        return 10L;
    }

    public void changeGoalPosition(){
        GameWebSocketController.changeGoal(getGameBoard().getSpaces(), getGameId());
    }

    public Map<String, Long> setBoardGoal(List<GameBoardSpace> spaces){
        Map<String, Long> response = new HashMap<>();
        GameBoardSpace oldGoal = findSpaceById(spaces, findGoal(spaces));
        oldGoal.setIsGoal(false); //NOSONAR
        Long newGoal;
        do{
            newGoal = (long) (Math.random() * 8 + 1); //NOSONAR
        } while (newGoal.equals(oldGoal.getSpaceId()) || newGoal == 2L || newGoal == 3L);
        findSpaceById(spaces, newGoal).setIsGoal(true); //NOSONAR
        response.put("result", newGoal);
        return response;
    }

    public void endOfWalkCheck(Player player, String color, GameBoardSpace currentSpace){
        player.setShipAct(player.getShipTemp());
        if ("Yellow".equals(color)){
            player.addLandYellow();
        }
        else if ("Catnami".equals(color) || "26".equals(currentSpace.getOnSpace())){
            player.addLandCat();
        }
        checkWinCondition(player);
    }

    public void maroonedCompanyShipUnlucky(Player player, int moves){
        int move15 = (moves >= 15) ? 1 : 0;
        if (move15==1){
            player.setShipTemp(1);
        }
        checkWinCondition(player);
    }

    public void checkWinCondition(Player player){
        switch (player.getWinCondition()) { //NOSONAR
            case "JackSparrow" -> GameWebSocketController.winCondiProgress(toWinCondi(player, 0, 1), player.getUserId(), getGameId());
            case "Marooned" -> {
                int mCash = (player.getCash() == 0) ? 1 : 0;
                int mCard = (player.getCardNames().isEmpty()) ? 1 : 0;
                int mItem = (player.getItemNames().isEmpty()) ? 1 : 0;
                GameWebSocketController.winCondiProgress(toWinCondi(player, mCash + mCard + mItem, 3), player.getUserId(), getGameId());
            }
            case "Golden" -> GameWebSocketController.winCondiProgress(toWinCondi(player,player.getLandYellow(), 7), player.getUserId(), getGameId());
            case "Drunk" -> GameWebSocketController.winCondiProgress(toWinCondi(player,player.getLandCat(), 3), player.getUserId(), getGameId());
            case "ThirdTime" -> GameWebSocketController.winCondiProgress(toWinCondi(player, player.getPassGoal(), 2), getGameId(), player.getUserId());
            case "Company" -> {
                int cash60 = (player.getCash()>=60) ? 1 : 0;
                GameWebSocketController.winCondiProgress(toWinCondi(player, cash60, 1), player.getUserId(), getGameId());
            }
            case "Ship" -> GameWebSocketController.winCondiProgress(toWinCondi(player, player.getShipAct(), 1), player.getUserId(), getGameId());

            case "Explorer" -> GameWebSocketController.winCondiProgress(toWinCondi(player, player.getLandedAll().size(), 61), player.getUserId(), getGameId());
            case "Unlucky" -> GameWebSocketController.winCondiProgress(toWinCondi(player, player.getLostCash(), 40), player.getUserId(), getGameId());
        }
    }

    private Map<String, Object> switchOverSpace(Player player, GameBoardSpace nextSpace, int movies, int moves, String color, Long currPosi, List<Long> listi){
        switch (color) {
            case "Junction" -> {return caseJunction(player, nextSpace, movies, moves, color, currPosi, listi);}
            case "Gate" -> {return caseGate(player, nextSpace, movies, moves, color, currPosi, listi);}
            case "SpecialItem" -> {return caseSpecialItem(player, movies, moves, color, listi);}
            default -> {
                return Collections.emptyMap();
            }
        }
    }

    public Map<String, Object> caseJunction(Player player, GameBoardSpace nextSpace, int movies, int moves, String color, Long currPosi, List<Long> listi){
        List<String> unlock = nextSpace.getNext();
        List<String> lock = new ArrayList<>();
        setMovesLeft(movies);
        GameWebSocketController.returnMoves(toMove(player, listi, moves, color), getGameId());
        GameWebSocketController.returnJunction(toJunction(player, currPosi, unlock, lock), getGameId(), player.getUserId());
        return Collections.emptyMap();
    }

    public Map<String, Object> caseGate(Player player, GameBoardSpace nextSpace, int movies, int moves, String color, Long currPosi, List<Long> listi){
        List<String> unlock = new ArrayList<>();
        List<String> lock = new ArrayList<>();
        setMovesLeft(movies);
        for (String item : player.getItemNames()) {
            if (item.equals("TheBrotherAndCo")) {
                unlock.add(nextSpace.getNext().get(0));
                lock.add(nextSpace.getNext().get(1));
                GameWebSocketController.returnMoves(toMove(player, listi, moves, color), getGameId());
                GameWebSocketController.returnJunction(toJunction(player, currPosi, unlock, lock), getGameId(), player.getUserId());
                return Collections.emptyMap();
            }
        }
        GameWebSocketController.returnMoves(toMove(player, listi, moves, color), getGameId());
        setHadJunctionForGoal(false);
        return move(getMovesLeft(), player.getPosition());
    }

    public Map<String, Object> caseSpecialItem(Player player, int movies, int moves, String color, List<Long> listi){
        setMovesLeft(movies);
        GameWebSocketController.returnMoves(toMove(player, listi, moves, color), getGameId());
        GameWebSocketController.returnUsables(toItem(player), getGameId());
        setHadJunctionForGoal(false);
        return move(getMovesLeft(), player.getPosition());
    }


    //TODO call when game is over and add winner status to achievementProgress
    public void updateAchievements(){
        for(Player player : players){
            AchievementService achievementService =  GetBean.getAchievementService();
            achievementService.updateAchievements(player.getAchievementProgress());
        }
    }


    /**
     * Helper for representing ws message dictionary
     * in case when move ends
     * in case when the move gets interrupted and needs data from frontend
     * in case when player gets an item
     */

    private Map<String, Object> toMove(Player player, List<Long> walkedSpaces, int initialMoves, String landedSpace){
        Map<String, Object> response = new HashMap<>();
        response.put("spaces", walkedSpaces);
        response.put("moves", initialMoves);
        response.put("spaceColor", landedSpace);
        Map<String, Object> retour = new HashMap<>();
        retour.put(player.getPlayerId().toString(), response);
        retour.put("movementType", "walk");
        return retour;
    }

    public Map<String, Object> toJunction(Player player, Long currSpace, List<String> nextUnlock, List<String> nextLock){
        Map<String, Object> response = new HashMap<>();
        response.put("playerId", player.getPlayerId().toString());
        response.put("currentSpace", currSpace);
        response.put("nextUnlockedSpaces", nextUnlock);
        response.put("nextLockedSpaces", nextLock);
        return response;
    }

    public Map<String, Object> toItem(Player player){
        Map<String, Object> retour = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        String randItem = randoItem();
        player.addItemNames(randItem);
        response.put("items", player.getItemNames());
        response.put("cards", player.getCardNames());
        retour.put(player.getPlayerId().toString(), response);
        return retour;
    }

    private Map<String, Object> toWinCondi(Player player, int progress, int needed){
        Map<String, Object> retour = new HashMap<>();
        retour.put("name", player.getWinCondition());
        retour.put("progress", progress);
        retour.put("total", needed);
        return retour;
    }

    public Map<String, Object> toMoney(Player player, int change) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Integer> details = new HashMap<>();
        int newAmount = Math.max(player.getCash()+change, 0);
        player.setCash(newAmount);
        details.put("newAmountOfMoney", newAmount);
        details.put("changeAmountOfMoney", change);
        response.put(player.getPlayerId().toString(), details);
        return response;
    }
/*
    private void printi(){
        System.out.println("P1:  Items: " + players[0].getItemNames() + "  Cards: " + players[0].getCardNames() + "  Cash: " + players[0].getCash() + "  Space: " + players[0].getPosition() + "  WinCondi: " + players[0].getWinCondition() + "  LostCash: " + players[0].getLostCash()); //NOSONAR
        System.out.println("P2:  Items: " + players[1].getItemNames() + "  Cards: " + players[1].getCardNames() + "  Cash: " + players[1].getCash() + "  Space: " + players[1].getPosition() + "  WinCondi: " + players[1].getWinCondition() + "  LostCash: " + players[1].getLostCash());
        System.out.println("P3:  Items: " + players[2].getItemNames() + "  Cards: " + players[2].getCardNames() + "  Cash: " + players[2].getCash() + "  Space: " + players[2].getPosition() + "  WinCondi: " + players[2].getWinCondition() + "  LostCash: " + players[2].getLostCash());
        System.out.println("P4:  Items: " + players[3].getItemNames() + "  Cards: " + players[3].getCardNames() + "  Cash: " + players[3].getCash() + "  Space: " + players[3].getPosition() + "  WinCondi: " + players[3].getWinCondition() + "  LostCash: " + players[3].getLostCash());
    }

 */
}
