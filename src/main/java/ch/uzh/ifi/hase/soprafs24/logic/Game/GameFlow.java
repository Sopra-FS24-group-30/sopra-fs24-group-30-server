package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;


public class GameFlow {


    private Player[] players = new Player[4];
    private GameBoard gameBoard;
    private Long turnPlayerId;

    public Long getTurnPlayerId() {
        return turnPlayerId;
    }

    public void setTurnPlayerId(Long turnPlayerId) {
        this.turnPlayerId = turnPlayerId;
    }
/*
    TODO:
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

    //Effecthandlers => return playerIds and values

    private void handleEffects(String itemName){}

    //give a player x amount of additional turns => takes the parameters, returns the amount of dice the player can throw
    private int giveTurns(JSONObject args){
        //TODO: make sure same persons turn and get reward if req met
        int amountDice = args.getInt("dice");
        return amountDice;
    }

    private void exchange(JSONObject args){
        JSONArray exchangees = args.getJSONArray("matches");
        JSONObject giveInfos = args.getJSONObject("give");
        JSONObject getInfos = args.getJSONObject("get");

        ArrayList<Integer> givePlayers = specialIds(getInfos.getString("player"));
        String giveType = giveInfos.getString("type");
        String giveSelection = giveInfos.getString("selection");
        Integer giveAmount = giveInfos.getInt("amount");

        ArrayList<Integer> getPlayer = specialIds(getInfos.getString("player"));
        String getType = getInfos.getString("type");
        String getSelection = getInfos.getString("selection");
        Integer getAmount = getInfos.getInt("amount");

    }

    //TODO: make overloaded method for choosen playerids => need to if else either get id from call or with sepcialIds
    private Hashtable<Long,Integer> updateMoney(JSONObject args){
        Hashtable<Long,Integer> playersPayMoney = effectivePayAmounts(args.getJSONObject("amount"));
        for(Long key : playersPayMoney.keySet()){
            this.players[Math.toIntExact(key)-1].addCash(playersPayMoney.get(key));
        }

        //TODO: send infos to fronted;
        return playersPayMoney;
    }

    /**
     * give in how much each player should pay in order to get how much they will pay based on how much cash they have
     * @param amounts the parameters to be processed
     * @return PlayerIds,Amount
     */

    private Hashtable<Long,Integer> effectivePayAmounts(JSONObject amounts){
        int totalPot = 0;
        ArrayList<Integer> potWinners = new ArrayList<>();
        Hashtable<Long,Integer> calculatedAmount= new Hashtable<>();
        ArrayList<Integer> playerIds = new ArrayList<>();
        Iterator<String> keys = amounts.keys();
        while(keys.hasNext()){
            String key = keys.next();
            playerIds = specialIds(key);
            Integer amount = moneyDescToNumber(amounts.getString(key));
            for (Integer id : playerIds) {
                if (amount == null){
                    potWinners.add(id);
                    calculatedAmount.put(Long.valueOf(id),0);
                }else if(amount < 0){
                    int toPay = checkCash(this.players[id-1].getPlayerId().intValue(),amount);
                    totalPot += toPay;
                    calculatedAmount.put(Long.valueOf(id),toPay);
                }else{
                    calculatedAmount.put(Long.valueOf(id),amount);
                }
            }
        }
        totalPot = totalPot * -1;
        for(Integer id : potWinners){
            calculatedAmount.put(Long.valueOf(id),totalPot/potWinners.size());
        }

        return calculatedAmount;
    }


    /**
     * helperfunction for calculating how much money is given
     * @param description the defined amount
     * @return the actual value, 1000 used for max
     */
    private Integer moneyDescToNumber(String description){
        if(description.equals("givenAmount")){
            return null;
        }
        else if (description.equals("everything")) {
            return 1000;
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
                playerIds.add((int) (long) this.turnPlayerId);
                break;
            case "others":
                for(int i=1;i<=4;i++){
                    if(i != (int) (long) this.turnPlayerId){
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
                int mate = (int) (long) this.turnPlayerId+2;
                if(mate > 4){
                    mate = mate % 4;
                }
                playerIds.add(mate);
                break;
            case "enemy":
                int current = (int) (long) this.turnPlayerId;
                if(current % 2 == 0){
                    playerIds.add(1);
                    playerIds.add(3);
                }else{
                    playerIds.add(2);
                    playerIds.add(4);
                }
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
    //TODO: send to frontend infos about money
    private ArrayList<Integer> givePlayerDice(JSONObject definition){

        int diceCount = definition.getInt("dice");
        int bonusCount = definition.getInt("bonusCount");
        int cashAmount = definition.getInt("money");
        ArrayList<Integer> diceThrows = new ArrayList<>();

        for(int i=0;i<diceCount;i++){
            diceThrows.add((int) (Math.random()*6+1));
        }

        for(int diceValue=1;diceValue<=6;diceValue++){
            if(Collections.frequency(diceThrows,diceValue) == bonusCount){
                players[this.turnPlayerId.intValue()].addCash(cashAmount);
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
        int playerCash = this.players[playerId-1].getCash();
        return (playerCash + cashAmount < 0) ? playerCash*-1 : cashAmount;
    }

    private void createBoard(){} //NOSONAR

    private void getWinCondition(){} //NOSONAR

    private void getUltimate(){} //NOSONAR

    private void addPlayer(Player player){
        this.players[(int) (long)player.getPlayerId()-1] = player;
    } //NOSONAR

    private void endTurn(Long playerId){
        //TODO: notify frontend about turn is done
    }

    private void useItem(){
    } //NOSONAR
    private void useUltimate(Ultimate ultimate){
    } //NOSONAR
    private void useCard(){} //NOSONAR


    public int throwDice(){
        return (int) (Math.random() * 6 + 1); //NOSONAR
    }

    //maybe split update player up into cash,item,card,posi?
    private void updatePlayer(){} //NOSONAR

    //normal walk
    public Map<String, Object> move(GameBoard gameBoard, Player player, int moves, long posi) {
        Long currPosi = posi;
        int movies = moves;
        List<GameBoardSpace> allSpaces = gameBoard.getSpaces(); //list of all spaces

        List<Long> listi = new ArrayList<>(); //list of spaceIds that player moves over
        String color = null; //color of next space

        GameBoardSpace currentSpace = new GameBoardSpace(); //space currently on
        List<String> nextSpaceIds; //list of next spaces of space currently on

        while (movies > 0) {
            currentSpace = findSpaceById(allSpaces, currPosi);
            nextSpaceIds = currentSpace.getNext(); //NOSONAR

            // if next space is linear (not junction/gate)
            if (nextSpaceIds.size() == 1) {
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
                        return toReturn(player, listi, moves, color);
                    }
                    //changed from: player.setCash(player.getCash() + 15)
                    GameWebSocketController.changeMoney(player, +15);
                }

                // "partial end of walk", check on what decision can be done.
                // walk can never end in these space types btw (junction, gate, specialItem)
                if (nextSpace.getOnSpace() == null) {
                    if (color.equals("Junction")){
                        // return the currently walked spaces, after getting a response, continue with the walk
                        // BUT HOW TO HANDLE RESPONSE OF JUNCTION NOSONAR
                        // like handled in SpaceEffects
                        return toReturn(player, listi, moves, color);
                    }
                    else if (color.equals("Gate")){
                        //check if brother exists
                        // - if yes then return the currently walked spaces (like junction)

                        // BUT HOW TO HANDLE RESPONSE OF GATE NOSONAR
                        // like handled in SpaceEffects
                        for (Item item : player.getItems()){
                            if (item.getItemName().equals("TheBrotherAndCo")){
                                return toReturn(player,listi,moves,color);
                            }
                        }
                        // - if no then not even ask, just continue
                        // prob handle with recursion instead of just continuing

                        // ugly ass Gate handling NOSONAR
                        currentSpace = findSpaceById(allSpaces, currPosi);
                        nextSpaceIds = currentSpace.getNext(); //NOSONAR
                        nextPosi = Long.parseLong(nextSpaceIds.get(0));
                        listi.add(nextPosi);
                        nextSpace = findSpaceById(allSpaces, nextPosi);
                        color = nextSpace.getColor(); //NOSONAR
                        currPosi = nextPosi;
                        player.setPosition(currPosi);
                    }
                    else if (color.equals("SpecialItem")){
                        //add an item to the player item list
                        SpaceEffects.specialItem(player);
                        return toReturn(player,listi,moves,color);
                    }
                    //send data to friend (via websocket)
                    //recursively call this functiön
                }
            }
            movies--;
        }

        //at the end of the walk
        if ("Yellow".equals(color)){
            player.setLandYellow(player.getLandYellow()+1);
        }
        else if ("CatNami".equals(color)){
            player.setLandCat(player.getLandCat()+1); //also include this on space 18
        }
        // THE SPACE EFFECT NOW/LATER??
        SpaceEffects.getSpaceEffectValue(currentSpace.getOnSpace());
        return toReturn(player, listi, moves, color);
    }



    //helper for finding Space by Id
    private GameBoardSpace findSpaceById(List<GameBoardSpace> spaces, Long spaceId) {
        for (GameBoardSpace space : spaces) {
            if (space.getSpaceId().equals(spaceId)) {
                return space;
            }
        }
        return null;
    }

    //helper for data representation as dict
    private Map<String, Object> toReturn(Player player, List<Long> walkedSpaces, int initialMoves, String landedSpace){
        Map<String, Object> data = new HashMap<>();
        data.put("spaces", walkedSpaces);
        data.put("moves", initialMoves);
        data.put("spaceColor", landedSpace);
        Map<String, Object> retour = new HashMap<>();
        retour.put(player.getPlayerId().toString(), data);
        return retour;
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
