package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.*;
import org.json.JSONObject;

import java.util.*;

public class GameFlow {

    private static final String[] allItems = {"TheBrotherAndCo", "MagicMushroom", "SuperMagicMushroom", "UltraMagicMushroom", "OnlyFansSub", "TreasureChest"};

    private static Player[] players = new Player[4];
    public static Player[] getPlayers() {
        return players;
    }
    public Player getPlayer(Integer playerId) {
        return players[playerId - 1];
    }

    private static GameBoard gameBoard;
    public static void setGameBoard() {
        GameFlow.gameBoard = GameWebSocketController.getCurrGame().getGameBoard();
    }

    private static Long turnPlayerId;
    public static Long getTurnPlayerId() {
        return turnPlayerId;
    }
    public static void setTurnPlayerId(Long turnoPlayerId) {
        GameFlow.turnPlayerId = turnoPlayerId;
    }

    private static int currentTurn;
    public static int getCurrentTurn() {
        return currentTurn;
    }
    public static void setCurrentTurn() {
        GameFlow.currentTurn = GameWebSocketController.getCurrGame().getRoundNum();
    }

    public GameFlow(){
        //setGameBoard();
        //setCurrentTurn();
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

    //TODO: add numbers for the starting

    /**
     * find the starting fields for the players
     * @param playerId
     * @return left or right starting field Id
     */
    private Long findStart(Integer playerId){
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
                return 10L;
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

        GameWebSocketController.returnMoves(moveData);
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
        GameWebSocketController.returnUsables(usableData);
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
                    //TODO: add function from Ta here
                    int select = (int) (Math.random()*playerItems.size());
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
                    int select = (int) Math.random()*playerCards.size()+1;
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
        GameWebSocketController.returnMoney(cashData);
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
            //TODO: get from frontend which number
            case "choice":
                playerIds.add(2);
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
    //TODO: send to frontend infos about money and call move with total
    public ArrayList<Integer> givePlayerDice(JSONObject definition){

        int diceCount = definition.getInt("dice");
        int bonusCount = definition.getInt("bonusCount");
        int cashAmount = definition.getInt("money");
        ArrayList<Integer> diceThrows = new ArrayList<>();

        for(int i=0;i<diceCount;i++){
            diceThrows.add((int) (Math.random()*6+1));
        }

        for(int diceValue=1;diceValue<=6;diceValue++){
            if(Collections.frequency(diceThrows,diceValue) == bonusCount){
                players[turnPlayerId.intValue()-1].addCash(cashAmount);
            }
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

    // update to next player
    private static Map<String, Object> nextPlayer() {
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

    //TODO: GAME OVER FUNCTION CHECK
    private static Map<String, Object> doGameOver(){
        return Collections.emptyMap();
    }

    public static List<Integer> throwDice() {
        return Collections.singletonList((int) (Math.random() * 6) + 1);
    }

    public static Map<String, Long> setBoardGoal(List<GameBoardSpace> spaces){
        Map<String, Long> response = new HashMap<>();
        GameBoardSpace oldGoal = findSpaceById(spaces, findGoal(spaces));
        oldGoal.setIsGoal(false);
        Long newGoal;
        do{
            newGoal = (long) (Math.random() * 8 + 1);
        } while (newGoal.equals(oldGoal.getSpaceId()));
        findSpaceById(spaces, newGoal).setIsGoal(true);
        response.put("result", newGoal);
        return response;
    }

    //normal walk
    public static Map<String, Object> move(int moves, long posi) {
        Player player = players[(int) (turnPlayerId-1)];
        Long currPosi = posi;
        int movies = moves;
        List<GameBoardSpace> allSpaces = gameBoard.getSpaces(); //list of all spaces

        List<Long> listi = new ArrayList<>(); //list of spaceIds that player moves over
        String color = null; //color of next space

        GameBoardSpace currentSpace = new GameBoardSpace(); //space currently on initialize
        List<String> nextSpaceIds; //list of next spaces of space currently on

        while (movies > 0) {
            currentSpace = findSpaceById(allSpaces, currPosi);
            nextSpaceIds = currentSpace.getNext(); //NOSONAR

            Long nextPosi = Long.parseLong(nextSpaceIds.get(0));
            listi.add(nextPosi);

            GameBoardSpace nextSpace = findSpaceById(allSpaces, nextPosi); // space next on
            color = nextSpace.getColor(); //NOSONAR

            //set player to next posi already
            currPosi = nextPosi;
            player.setPosition(currPosi);

            // check if game is over, or player gets cash
            if ("BlueGoal".equals(color) && nextSpace.getIsGoal()) { //NOSONAR
                if (player.getCanWin()) {
                    // GAME OVER
                    GameWebSocketController.juncMove(toMove(player, listi, moves, color));
                    GameWebSocketController.endy(doGameOver());
                    return Collections.emptyMap();
                }
                //changed from: player.setCash(player.getCash() + 15)
                GameWebSocketController.changeMoney(player, +15);
                GameWebSocketController.changeGoal(allSpaces);
            }

            // "partial end of walk", check on what decision can be done.
            // walk can never end in these space types btw (junction, gate, specialItem)
            if (nextSpace.getOnSpace() == null) {
                switch (color) {
                    case "Junction" -> {
                        List<String> unlock = findSpaceById(allSpaces, currPosi).getNext();
                        List<String> lock = new ArrayList<>();
                        GameWebSocketController.setMovesLeft(movies);
                        GameWebSocketController.juncMove(toMove(player, listi, moves, color));
                        GameWebSocketController.juncJunc(toJunction(player, currPosi, unlock, lock));
                        return Collections.emptyMap();
                    }
                    case "Gate" -> {
                        List<String> unlock = new ArrayList<>();
                        List<String> lock = new ArrayList<>();
                        GameWebSocketController.setMovesLeft(movies);
                        for (String item : player.getItemNames()) {
                            if (item.equals("TheBrotherAndCo")) {
                                unlock.add(findSpaceById(allSpaces, currPosi).getNext().get(0));
                                lock.add(findSpaceById(allSpaces, currPosi).getNext().get(1));
                                GameWebSocketController.juncMove(toMove(player, listi, moves, color));
                                GameWebSocketController.juncJunc(toJunction(player, currPosi, unlock, lock));
                                return Collections.emptyMap();
                            }
                        }
                        GameWebSocketController.juncMove(toMove(player, listi, moves, color));
                        move(GameWebSocketController.getMovesLeft(), player.getPosition());
                    }
                    case "SpecialItem" -> {
                        GameWebSocketController.setMovesLeft(movies);
                        GameWebSocketController.specItem(toItem(player));
                        GameWebSocketController.juncMove(toMove(player, listi, moves, color));
                        move(GameWebSocketController.getMovesLeft(), player.getPosition());
                    }
                    default -> {
                        return Collections.emptyMap();
                    }
                }
            }
            movies--;
        }

        //at the end of the walk
        if ("Yellow".equals(color)){
            player.setLandYellow(player.getLandYellow()+1);
        }
        else if ("CatNami".equals(color) || "26".equals(currentSpace.getOnSpace())){
            player.setLandCat(player.getLandCat()+1);
        }

        GameWebSocketController.juncMove(toMove(player, listi, moves, color));
        //TODO: Space Effect
        GameWebSocketController.newPlayer(nextPlayer());
        //check if Game is over
        if (currentTurn >= 21){
            GameWebSocketController.endy(doGameOver());
        }
        return Collections.emptyMap();
    }


    // helper for finding Space by Id
    private static GameBoardSpace findSpaceById(List<GameBoardSpace> spaces, Long spaceId) {
        for (GameBoardSpace space : spaces) {
            if (space.getSpaceId().equals(spaceId)) {
                return space;
            }
        }
        return null;
    }

    //helper to find goal and get Id
    private static Long findGoal(List<GameBoardSpace> spaces){
        for (GameBoardSpace space : spaces){
            if (space.getIsGoal()){
                return space.getSpaceId();
            }
        }
        return null;
    }

    //helper for data representation as dict, move
    private static Map<String, Object> toMove(Player player, List<Long> walkedSpaces, int initialMoves, String landedSpace){
        Map<String, Object> data = new HashMap<>();
        data.put("spaces", walkedSpaces);
        data.put("moves", initialMoves);
        data.put("spaceColor", landedSpace);
        Map<String, Object> retour = new HashMap<>();
        retour.put(player.getPlayerId().toString(), data);
        retour.put("movementType", "walk");
        return retour;
    }

    //helper for data representation as dict, junc
    private static Map<String, Object> toJunction(Player player, Long currSpace, List<String> nextUnlock, List<String> nextLock){
        Map<String, Object> data = new HashMap<>();
        data.put("playerId", player.getPlayerId().toString());
        data.put("currentSpace", currSpace);
        data.put("nextUnlockedSpaces", nextUnlock);
        data.put("nextLockedSpaces", nextLock);
        return data;
    }

    //helper for data representation as dict, item
    private static Map<String, Object> toItem(Player player){
        Map<String, Object> retour = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("items", randoItem());
        data.put("cards", new ArrayList<>());
        retour.put(player.getPlayerId().toString(), data);
        return retour;
    }

    //get a random Item
    private static String randoItem(){
        return allItems[(int) (Math.random()*allItems.length)];
    }

    private void turn(){} //NOSONAR


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
