package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Effects.Getem;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.*;
import ch.uzh.ifi.hase.soprafs24.repository.AchievementRepository;
import ch.uzh.ifi.hase.soprafs24.service.AchievementService;
import org.json.JSONObject;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.security.SecureRandom;
import java.util.*;

public class GameFlow {

    protected static final String[] allItems = Getem.getItems().keySet().toArray(new String[0]);
    protected static final String[] allCards = Getem.getCards().keySet().toArray(new String[0]);

    private Long gameId;
    private Player[] players = new Player[4];
    private GameBoard gameBoard;
    private Long turnPlayerId;
    private int currentTurn;
    private int movesLeft;
    private JSONObject choices;

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
    public void setGameBoard(Long lobbyId) {
        this.gameBoard = GameWebSocketController.getCurrGame(lobbyId).getGameBoard();
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

    public GameFlow(){}

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


    //TODO: let handle choice

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
                return Long.valueOf(choices.getString("field"));
            case "randomPlayer":
                int player;
                do{
                    player = (int) (Math.random() * 3 + 1);
                }while (player == turnPlayerId);
                return players[player-1].getPosition();
            default:
                return (long) Integer.parseInt(fieldId);
        }
    }

    /**
     * teleport players to a field
     * @param args parameters of the updatepositions effect
     * @return key: playerId, value: the new fieldId where the player gets teleported to
     */
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
        }

        MoveData moveData = new MoveData(updatedPositions.get(1),updatedPositions.get(2),updatedPositions.get(3),updatedPositions.get(4));

        GameWebSocketController.returnMoves(moveData,gameId);
    }


    //TODO: add support for giving items to multiple people as of now can only exchange with one
    //TODO: get the itemNames to be exchanged

    /**
     * exchange usables between players
     * @param args parameters for the exchange effect
     * @param exchanges choices from frontend
     */
    public void exchange(JSONObject args, HashMap<Integer,ArrayList<String>> exchanges){
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

        giveUsables.addAll(getType(exchanges, givePlayers, giveType, giveSelection, giveAmount));
        getUsables.addAll(getType(exchanges, getPlayers, getType, getSelection, getAmount));


        for(int playerId : givePlayers){
            updateUsables(playerId,getUsables,getType);
        }
        for(int playerId : getPlayers){
            updateUsables(playerId,giveUsables,giveType);
        }

        UsableData usableData = new UsableData();
        usableData.setItems(players[0].getItemNames(),players[1].getItemNames(),players[2].getItemNames(),players[3].getItemNames());
        usableData.setCards(players[0].getCardNames(),players[1].getCardNames(),players[2].getCardNames(),players[3].getCardNames());
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
                players[playerId-1].addItemNames(usables);
                break;
            case "card":
                players[playerId-1].addCardNames(usables);
                break;
        }
    }

    /**
     * get the list with usables based wheter item or card is specified and remove them from the player
     * @param exchanges choices from frontend
     * @param exchangePlayers players for which usables need to be fetched
     * @param type item or card
     * @param selection all, random, choice
     * @param amount in case of choice or random, how many usables are to be fetched
     * @return gives back the usables
     */
    private ArrayList<String> getType(HashMap<Integer, ArrayList<String>> exchanges, ArrayList<Integer> exchangePlayers, String type, String selection, Integer amount) {
        if(type != null){
            switch (type){ //NOSONAR
                case "item":
                    for(Integer player : exchangePlayers){
                        return getSelectedItems(exchanges,selection,player,amount);
                    }
                    break;
                case "card":
                    for(Integer player : exchangePlayers){
                        return (getSelectedCards(exchanges,selection,player,amount));
                    }
            }
        }
        return new ArrayList<String>();
    }


    //TODO: give infos to frontend what was removed

    /**
     * remove the items from the player and give the items gained this way back in a list
     * @param exchanges selections from frontend in case of choice
     * @param selection type of selections
     * @param playerid ID of the concerning player
     * @param amount how many items are given
     * @return all the items which are ready for exchange
     */
    private ArrayList<String> getSelectedItems(HashMap<Integer,ArrayList<String>> exchanges, String selection, int playerid, Integer amount){
        ArrayList<String> returnItems = new ArrayList<>();
        if(selection == null){
            return returnItems;
        }
        ArrayList<String> playerItems = players[playerid-1].getItemNames();
        switch(selection){ //NOSONAR
            case "random":
                for(int i = 0; i<amount;i++){
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
                returnItems.addAll(exchanges.get(playerid));
                players[playerid-1].removeItemNames(exchanges.get(playerid));
                break;
        }

        return returnItems;
    }

    /**
     * remove the cards from the player and give the cards gained this way back in a list
     * @param exchanges selections from frontend in case of choice
     * @param selection type of selections
     * @param playerid ID of the concerning player
     * @param amount how many cards are given
     * @return all the items which are ready for exchange
     */
    private ArrayList<String> getSelectedCards(HashMap<Integer,ArrayList<String>> exchanges, String selection, int playerid, Integer amount){
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
                returnCards.addAll(exchanges.get(playerid));
                players[playerid-1].removeCardNames(exchanges.get(playerid));
                break;
        }
        return returnCards;
    }

    /**
     * update the money of players
     * @param args parameters for the updatemoney effect
     * @return key: playerId, value: the new amount of money the player has
     */
    //TODO: make overloaded method for choosen playerids => need to if else either get id from call or with sepcialIds
    public void updateMoney(JSONObject args){
        String type = args.getString("type");
        Hashtable<Long,Integer> playersPayMoney;

        playersPayMoney = effectivePayAmounts(args.getJSONObject("amount"),type);

        CashData cashData = new CashData();
        cashData.setPlayersNewCash(players[0].getCash(),players[1].getCash(),players[2].getCash(),players[3].getCash());
        cashData.setPlayersChangeAmount(playersPayMoney.get(1L),playersPayMoney.get(2L),playersPayMoney.get(3L),playersPayMoney.get(4L));
        GameWebSocketController.returnMoney(cashData,gameId);
    }

    public Map<String, Object> updateCardPositions (JSONObject args, int count){
        //System.out.println(args);
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
                throw new RuntimeException("the card with type " + category + " does not exist");
        }

        return Collections.emptyMap();
    }

    /**
     * give in how much each player should pay in order to get how much they will pay based on how much cash they have
     * @param amounts the parameters to be processed
     * @return PlayerIds,Amount
     */

    //TODO: Refactor to be nicer
    private Hashtable<Long,Integer> effectivePayAmounts(JSONObject amounts, String type){//NOSONAR
        int totalPot = 0;
        ArrayList<Integer> potWinners = new ArrayList<>();
        Hashtable<Long,Integer> calculatedAmount = new Hashtable<>();
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
                            int toPayAbsolute = checkCash(players[id-1].getPlayerId().intValue(),amount);
                            totalPot += toPayAbsolute;
                            players[id-1].setCash(players[id-1].getCash()+toPayAbsolute);
                            calculatedAmount.put(Long.valueOf(id),amount);
                            break;
                        case "relative":
                            int toPayRelative = (int) (players[id-1].getCash() / 100.0 * amount);
                            totalPot += toPayRelative;
                            players[id-1].setCash(players[id-1].getCash()+toPayRelative);
                            calculatedAmount.put(Long.valueOf(id),amount);
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
                            calculatedAmount.put(Long.valueOf(id),amount);
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
            return players[playerId-1].getCash();
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
                CashData cashData = new CashData();
                int newCash = players[turnPlayerId.intValue()].getCash()+cashAmount;
                cashData.setPlayerAmountAndUpdate(turnPlayerId.intValue(),newCash,cashAmount);
                GameWebSocketController.returnMoney(cashData,gameId);
            }
        }
        DiceData diceData = new DiceData(diceThrows);
        GameWebSocketController.returnDice(diceData,gameId);
        int totalAmount = 0;
        for(Integer dice : diceThrows){
            totalAmount += dice;
        }
        move(totalAmount,getPlayer(turnPlayerId.intValue()).getPosition());
    }

    public ArrayList<Integer> throwDice(int amount){
        ArrayList<Integer> diceThrows = new ArrayList<>();
        for(int i=0;i<amount;i++){
            diceThrows.add((int) (Math.random()*6+1));
        }
        return diceThrows;
    }

    /**
     * check if the player can pay if not reduce the amount to the maximum payable
     * @param playerId id of the player
     * @param cashAmount cash amount if has to pay negative amount
     * @return amount the player can pay
     */
    private int checkCash(int playerId, int cashAmount){
        int playerCash = players[playerId-1].getCash();
        return (playerCash + cashAmount < 0) ? playerCash*-1 : cashAmount;
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

    private List<Long> findMostCash(Player[] players){
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

    private Map<String, Object> nextPlayer() {
        long maxi = 4L;
        turnPlayerId++;
        if (turnPlayerId > maxi) {
            currentTurn++;
            turnPlayerId = 1L;
        }
        Map<String, Object> retour = new HashMap<>();
        retour.put("currentTurn", currentTurn);
        retour.put("activePlayer", turnPlayerId.toString());
        return retour;
    }

    public Map<String, Object> move(int moves, long posi) {
        //currentTurn = 20;
        Player player = players[(int) (turnPlayerId-1)];
        Long currPosi = posi;
        int movies = moves;
        List<GameBoardSpace> allSpaces = getGameBoard().getSpaces();

        List<String> beginCheckWin = new ArrayList<>(Arrays.asList("Marroned", "Company", "Ship"));

        if (beginCheckWin.contains(player.getWinCondition())){ //NOSONAR
            maroonedCompanyShip(player, moves);
        }

        List<Long> listi = new ArrayList<>();

        GameBoardSpace currentSpace = findSpaceById(allSpaces, currPosi);
        List<String> nextSpaceIds = currentSpace.getNext(); //NOSONAR
        Long nextPosi = Long.parseLong(nextSpaceIds.get(0));
        GameBoardSpace nextSpace = findSpaceById(allSpaces, nextPosi);
        String color = currentSpace.getColor();

        //check if game is over, or player gets cash, in case the player moves 0
        if (moves==0 && "BlueGoal".equals(color) && Boolean.TRUE.equals(currentSpace.getIsGoal())){
            return checkGoalGameOver(color, player, listi, movies, moves, allSpaces);
        }

        while (movies > 0) {
            currentSpace = findSpaceById(allSpaces, currPosi);
            nextSpaceIds = currentSpace.getNext(); //NOSONAR
            nextPosi = Long.parseLong(nextSpaceIds.get(0));
            listi.add(nextPosi);
            nextSpace = findSpaceById(allSpaces, nextPosi);
            color = nextSpace.getColor(); //NOSONAR

            currPosi = nextPosi;
            player.setPosition(currPosi);

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

        GameWebSocketController.juncMove(toMove(player, listi, moves, color), getGameId());
        System.out.println("endee  " + toMove(player, listi, moves, color));

        System.out.println("P1:  Items: " + players[0].getItemNames() + "  Cards: " + players[0].getCardNames() + "  Cash: " + players[0].getCash() + "  Space: " + players[0].getPosition() + "  WinCondi: " + players[0].getWinCondition());
        System.out.println("P2:  Items: " + players[1].getItemNames() + "  Cards: " + players[1].getCardNames() + "  Cash: " + players[1].getCash() + "  Space: " + players[1].getPosition() + "  WinCondi: " + players[1].getWinCondition());
        System.out.println("P3:  Items: " + players[2].getItemNames() + "  Cards: " + players[2].getCardNames() + "  Cash: " + players[2].getCash() + "  Space: " + players[2].getPosition() + "  WinCondi: " + players[2].getWinCondition());
        System.out.println("P4:  Items: " + players[3].getItemNames() + "  Cards: " + players[3].getCardNames() + "  Cash: " + players[3].getCash() + "  Space: " + players[3].getPosition() + "  WinCondi: " + players[3].getWinCondition());
        if (moves == 0) {
            (Spaces.runLandOns.get(currentSpace.getOnSpace())).apply(GameWebSocketController.getGameFlow(gameId)); //NOSONAR
        } else{
            ( Spaces.runLandOns.get(nextSpace.getOnSpace())).apply(GameWebSocketController.getGameFlow(gameId)); //NOSONAR
        }
        System.out.println("P1:  Items: " + players[0].getItemNames() + "  Cards: " + players[0].getCardNames() + "  Cash: " + players[0].getCash() + "  Space: " + players[0].getPosition() + "  WinCondi: " + players[0].getWinCondition());
        System.out.println("P2:  Items: " + players[1].getItemNames() + "  Cards: " + players[1].getCardNames() + "  Cash: " + players[1].getCash() + "  Space: " + players[1].getPosition() + "  WinCondi: " + players[1].getWinCondition());
        System.out.println("P3:  Items: " + players[2].getItemNames() + "  Cards: " + players[2].getCardNames() + "  Cash: " + players[2].getCash() + "  Space: " + players[2].getPosition() + "  WinCondi: " + players[2].getWinCondition());
        System.out.println("P4:  Items: " + players[3].getItemNames() + "  Cards: " + players[3].getCardNames() + "  Cash: " + players[3].getCash() + "  Space: " + players[3].getPosition() + "  WinCondi: " + players[3].getWinCondition());

        endOfWalkCheck(player, color, currentSpace, moves);

        GameWebSocketController.newPlayer(nextPlayer(), getGameId());

        //check if Game is over
        if (currentTurn >= 21){
            GameWebSocketController.endy(doGameOverMaxTurns(findMostCash(players)), getGameId());
            System.out.println(doGameOverMaxTurns(findMostCash(players)));
        }

        return Collections.emptyMap();
    }



    /**
     *
     * GameOver related functions
     *
     */
    private Map<String, Object> checkGoalGameOver(String color, Player player, List<Long> listi, int movies, int moves, List<GameBoardSpace> allSpaces){
        setMovesLeft(movies);
        if (player.getCanWin()) {
            GameWebSocketController.juncMove(toMove(player, listi, moves, color), getGameId());
            System.out.println("canwin  " + toMove(player, listi, moves, color));
            // GAME OVER
            GameWebSocketController.endy(doGameOverWinCondi(player), getGameId());
            System.out.println(doGameOverWinCondi(player));
            return Collections.emptyMap();
        }
        GameWebSocketController.juncMove(toMove(player, listi, moves, color), getGameId());
        GameWebSocketController.changeCash(toMoney(player, +15), getGameId());
        System.out.println("canotwin  " + toMove(player, listi, moves, color));

        if (player.getWinCondition().equals("ThirdTime")) {
            player.addPassGoal();
            GameWebSocketController.winCondiProgress(toWinCondi(player, player.getPassGoal(), 2), getGameId(), player.getPlayerId());
            System.out.println(toWinCondi(player, player.getPassGoal(), 2));
        }

        GameWebSocketController.changeGoal(allSpaces, getGameId());
        if (movies <= 0){
            return Collections.emptyMap();
        }
        return move(getMovesLeft(), player.getPosition());
    }

    private Map<String, Object> doGameOverWinCondi(Player player){
        Map<String, Object> mappi = new HashMap<>();
        Set<String> winners = new HashSet<>();
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

        if (!player.getTeammateId().equals(jack)){
            winners.add(player.getTeammateId().toString());
            if (jack!=50L){
                winners.add(jack.toString());
            }
        }
        mappi.put("winners", winners); //NOSONAR
        mappi.put("reason", reason); //NOSONAR

        return mappi;
    }

    private Map<String, Object> doGameOverMaxTurns(List<Long> rich) {
        Map<String, Object> mappi = new HashMap<>();
        Set<String> winners = new HashSet<>();
        List<String> reason = new ArrayList<>();

        for (Player player : players) {
            if (player.getWinCondition().equals("JackSparrow")) {
                winners.add(players[player.getPlayerId().intValue() - 1].getTeammateId().toString());
                reason.add(player.getPlayerId().toString());
                reason.add("JackSparrow");
                mappi.put("winners", winners);
                mappi.put("reason", reason);
                return mappi;
            }
        }

        if (rich.size() == 1) {
            winners.add(rich.get(0).toString());
            winners.add(players[rich.get(0).intValue() - 1].getTeammateId().toString());
            reason.add(rich.get(0).toString());
            reason.add("maxCash");
            mappi.put("winners", winners);
            mappi.put("reason", reason);
            return mappi;
        }

        int randNum = (int) (Math.random() * rich.size()); //NOSONAR
        winners.add(rich.get(randNum).toString());
        winners.add(players[rich.get(randNum).intValue() - 1].getTeammateId().toString());
        reason.add(rich.get(randNum).toString());
        reason.add("maxCash");

        mappi.put("winners", winners);
        mappi.put("reason", reason);
        return mappi;
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

    private Long findGoal(List<GameBoardSpace> spaces){
        for (GameBoardSpace space : spaces){
            if (Boolean.TRUE.equals(space.getIsGoal())){
                return space.getSpaceId();
            }
        }
        return null;
    }

    public Map<String, Long> setBoardGoal(List<GameBoardSpace> spaces){
        Map<String, Long> response = new HashMap<>();
        GameBoardSpace oldGoal = findSpaceById(spaces, findGoal(spaces));
        oldGoal.setIsGoal(false); //NOSONAR
        Long newGoal;
        do{
            newGoal = (long) (Math.random() * 8 + 1); //NOSONAR
        } while (newGoal.equals(oldGoal.getSpaceId()));
        findSpaceById(spaces, newGoal).setIsGoal(true); //NOSONAR
        response.put("result", newGoal);
        return response;
    }

    private void endOfWalkCheck(Player player, String color, GameBoardSpace currentSpace, int moves){
        if ("Yellow".equals(color)){
            player.addLandYellow();
            if (player.getWinCondition().equals("Golden")){
                GameWebSocketController.winCondiProgress(toWinCondi(player,player.getLandYellow(), 7), player.getPlayerId(), getGameId());
                System.out.println(toWinCondi(player, player.getLandYellow(), 7));
            }
        }
        else if ("Catnami".equals(color) || "26".equals(currentSpace.getOnSpace())){
            player.addLandCat();
            if (player.getWinCondition().equals("Drunk")){
                GameWebSocketController.winCondiProgress(toWinCondi(player,player.getLandCat(), 3), player.getPlayerId(), getGameId());
                System.out.println(toWinCondi(player, player.getLandCat(), 3));
            }
        }
        if (player.getWinCondition().equals("Marooned") || player.getWinCondition().equals("Company")){
            maroonedCompanyShip(player, moves);
        }
        //TODO when win condition gets shuffled, how to send to frontend the new updated progress
    }

    private void maroonedCompanyShip(Player player, int moves){
        switch (player.getWinCondition()){ //NOSONAR
            case "Marooned" -> {
                int mCash = (player.getCash() == 0) ? 1 : 0;
                int mCard = (player.getCardNames().isEmpty()) ? 1 : 0;
                int mItem = (player.getItemNames().isEmpty()) ? 1 : 0;
                GameWebSocketController.winCondiProgress(toWinCondi(player, mCash + mCard + mItem, 3), player.getPlayerId(), getGameId());
                System.out.println(toWinCondi(player, mCash + mCard + mItem, 3));
            }
            case "Company" -> {
                int cash60 = (player.getCash()>=60) ? 1 : 0;
                GameWebSocketController.winCondiProgress(toWinCondi(player, cash60, 1), player.getPlayerId(), getGameId());
                System.out.println(toWinCondi(player, cash60, 1));
            }
            case "Ship" -> {
                if (!player.getCanWinner()){
                    int move15 = (moves >= 15) ? 1 : 0;
                    if (move15==1){
                        player.setCanWinner(true);
                    }
                    GameWebSocketController.winCondiProgress(toWinCondi(player, move15, 1), player.getPlayerId(), getGameId());
                    System.out.println(toWinCondi(player, move15, 1));
                }
            }
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

    private Map<String, Object> caseJunction(Player player, GameBoardSpace nextSpace, int movies, int moves, String color, Long currPosi, List<Long> listi){
        List<String> unlock = nextSpace.getNext();
        List<String> lock = new ArrayList<>();
        setMovesLeft(movies);
        GameWebSocketController.juncMove(toMove(player, listi, moves, color), getGameId());
        GameWebSocketController.juncJunc(toJunction(player, currPosi, unlock, lock), player.getPlayerId(), getGameId());
        System.out.println("junctioon  " + toMove(player, listi, moves, color));
        System.out.println(toJunction(player, currPosi, unlock, lock));
        return Collections.emptyMap();
    }

    private Map<String, Object> caseGate(Player player, GameBoardSpace nextSpace, int movies, int moves, String color, Long currPosi, List<Long> listi){
        List<String> unlock = new ArrayList<>();
        List<String> lock = new ArrayList<>();
        setMovesLeft(movies);
        for (String item : player.getItemNames()) {
            if (item.equals("TheBrotherAndCo")) {
                unlock.add(nextSpace.getNext().get(0));
                lock.add(nextSpace.getNext().get(1));
                GameWebSocketController.juncMove(toMove(player, listi, moves, color), getGameId());
                GameWebSocketController.juncJunc(toJunction(player, currPosi, unlock, lock), player.getPlayerId(), getGameId());
                System.out.println("gateBro  " + toMove(player, listi, moves, color));
                System.out.println(toJunction(player, currPosi, unlock, lock));
                return Collections.emptyMap();
            }
        }
        GameWebSocketController.juncMove(toMove(player, listi, moves, color), getGameId());
        System.out.println("gateNoBro  " + toMove(player, listi, moves, color));
        return move(getMovesLeft(), player.getPosition());
    }

    private Map<String, Object> caseSpecialItem(Player player, int movies, int moves, String color, List<Long> listi){
        setMovesLeft(movies);
        GameWebSocketController.juncMove(toMove(player, listi, moves, color), getGameId());
        GameWebSocketController.specItem(toItem(player), getGameId());
        System.out.println("specitem  " + toMove(player, listi, moves, color));
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

    private Map<String, Object> toJunction(Player player, Long currSpace, List<String> nextUnlock, List<String> nextLock){
        Map<String, Object> response = new HashMap<>();
        response.put("playerId", player.getPlayerId().toString());
        response.put("currentSpace", currSpace);
        response.put("nextUnlockedSpaces", nextUnlock);
        response.put("nextLockedSpaces", nextLock);
        return response;
    }

    private Map<String, Object> toItem(Player player){
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

    private Map<String, Object> toMoney(Player player, int change) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Integer> details = new HashMap<>();
        int newAmount = Math.max(player.getCash()+change, 0);
        player.setCash(newAmount);
        details.put("newAmountOfMoney", newAmount);
        details.put("changeAmountOfMoney", change);
        response.put(player.getPlayerId().toString(), details);
        return response;
    }

    private void run(){} //NOSONAR
    private void setup(){} //NOSONAR
    private void teardown(){} //NOSONAR
    private void flow(){} //NOSONAR


    /*
    TODO SETUP NOSONAR
    Player kreieren
    Teams assigne
    Spieler reihefolg
    ultimate wählen
    win condition wählen
    Board laden/screen lade

    TODO TURNS NOSONAR
    items/ultimates
    update to player positions
    updates to player status
    würfeln/cards
    Updates to player positions
    updates to player status
    give turn to next player

    TODO ENDGAME NOSONAR
    update userprofiles
    remove players
    destroy gameboard
    display winners

    TODO ALWAYS NOSONAR
    display cash
    voice chat
    display items
    display cards
    display other players
    display win/ultimate (only for self)
     */
}
