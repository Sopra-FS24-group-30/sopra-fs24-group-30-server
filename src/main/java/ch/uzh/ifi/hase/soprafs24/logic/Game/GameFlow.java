package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
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

    //TODO: make overloaded method for choosen playerids => need to if else either get id from call or with sepcialIds
    private Hashtable<Long,Integer> updateMoney(JSONObject args){
        Hashtable<Long,Integer> playersMoney = new Hashtable<>();
        JSONObject amounts = args.getJSONObject("amount");

        //find the amounts
        HashMap<Integer,Integer> playerPays = new HashMap<>();
        ArrayList<Integer> payingId = new ArrayList<>();
        Iterator<String> keys = amounts.keys();
        while (keys.hasNext()){
            String key = keys.next();
            Integer moneyAmount = moneyDescToNumber(amounts.getString(key));
            payingId = specialIds(key);
            for (Integer id : payingId) {
                playerPays.put(id, moneyAmount);
            }
        }

        return new Hashtable<Long,Integer>();
    }

    private HashMap<Integer,Integer> effectivePayAmounts(JSONObject amounts){
        int totalPot = 0;
        ArrayList<Integer> potWinners = new ArrayList<>();
        HashMap<Integer,Integer> calculatedAmount= new HashMap<>();
        ArrayList<Integer> playerIds = new ArrayList<>();
        Iterator<String> keys = amounts.keys();
        while(keys.hasNext()){
            String key = keys.next();
            playerIds = specialIds(key);
            Integer amount = moneyDescToNumber(amounts.getString(key));
            for (Integer id : playerIds) {
                if (amount == null){
                    potWinners.add(id);
                    calculatedAmount.put(id,amount);
                }else if(amount < 0){
                    Integer toPay = amount < this.players[id-1].getCash() ? amount : this.players[id-1].getCash();
                    totalPot += toPay;
                    calculatedAmount.put(id,amount);
                }else{
                    calculatedAmount.put(id,amount);
                }
            }
        }
        for(Integer id : potWinners){
            calculatedAmount.put(id,totalPot/potWinners.size());
        }

        return calculatedAmount;
    }

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

    //convert the specialIds to actual ids
    private ArrayList<Integer> specialIds(String specialId){

        ArrayList<Integer> playerIds = new ArrayList<>();

        switch (specialId){
            case "current":
                playerIds.add((int) (long) this.turnPlayerId);
                break;
            case "other":
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



    //throws multiple dice and gives money if the numbers match, returns the summed number which was thrown
    private Integer multiDiceThrow(Integer diceCount){
        int playerId = (int)(long)this.turnPlayerId-1;
        int firstMove = (int) (Math.random()*6+1);
        int secondMove = (int) (Math.random()*6+1);
        int moveCount = firstMove + secondMove;
        switch(diceCount){
            case 2:
                if(firstMove == secondMove){
                    players[playerId].addCash(10);
                }
                break;
            case 3:
                int additionalMove = (int) (Math.random()*6+1);
                moveCount += additionalMove;
                if(firstMove == secondMove && firstMove == additionalMove){
                    players[playerId].addCash(30);
                }
                break;
            case 4:
                int thirdMove = (int) (Math.random()*6+1);
                int fourthMove = (int) (Math.random()*6+1);
                moveCount += thirdMove + fourthMove;
                if(firstMove == secondMove && firstMove == thirdMove && firstMove == fourthMove){
                    players[playerId].addCash(69);
                }
                break;
        }
        return moveCount;
    }

    private void createBoard(){} //NOSONAR

    private void getWinCondition(){} //NOSONAR

    private void getUltimate(){} //NOSONAR

    private void addPlayer(){} //NOSONAR

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
    //TODO: Gameboard useneh und als class variable? git nume einisch und wird überall brucht?
    public Map<String, Object> updatePlayerPosition(GameBoard gameBoard, Player player, int moves, long posi) {
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
                    player.setCash(player.getCash() + 15);
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
            player.setLandCat(player.getLandCat()+1);
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
