package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import java.util.*;

public class GameFlow {

    private static final String[] allItems = {"TheBrotherAndCo", "MagicMushroom", "SuperMagicMushroom", "UltraMagicMushroom", "OnlyFansSub", "TreasureChest"};

    private static Player[] players = new Player[4];
    private static GameBoard gameBoard;
    private static Long turnPlayerId;
    public Long getTurnPlayerId() {
        return turnPlayerId;
    }

    public void setTurnPlayerId(Long turnPlayerId) {
        GameFlow.turnPlayerId = turnPlayerId;
    }

    private void createBoard(){} //NOSONAR

    private void getWinCondition(){} //NOSONAR

    private void getUltimate(){} //NOSONAR

    private void addPlayer(){} //NOSONAR

    private void useItem(){} //NOSONAR
    private void useCard(){} //NOSONAR

    public static List<Integer> throwDice(){
        return Collections.singletonList((int) (Math.random() * 6) + 1);
    }

    //maybe split update player up into cash,item,card,posi?
    private void updatePlayer(){} //NOSONAR

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

    public static Player getPlayerById(Long turnPlayerId){
        for (Player player : players){
            if (player.getPlayerId().equals(turnPlayerId)){
                return player;
            }
        }
        return null;
    }

    //normal walk
    public static Map<String, Object> move(int moves, long posi) {
        Player player = getPlayerById(turnPlayerId);
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
                    return toMove(player, listi, moves, color);
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

        return toMove(player, listi, moves, color);
        //turn over!!! next player
    }

    //helper for finding Space by Id
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
