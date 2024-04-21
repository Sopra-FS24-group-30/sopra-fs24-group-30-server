package ch.uzh.ifi.hase.soprafs24.logic.Game;

import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameFlow {

    private void createBoard(){}

    private void getWinCondition(){}

    private void getUltimate(){}

    private void addPlayer(){}

    private void useItem(){}
    private void useCard(){}

    public int throwDice(){
        return (int) (Math.random() * 6 + 1);
    }

    //maybe split update player up into cash,item,card,posi?
    private void updatePlayer(){}

    //normal walk
    public Map<String, Object> updatePlayerPosition(GameBoard gameBoard, Player player, int moves, long posi) {
        //Long currPosi = player.getPosition();
        Long currPosi = posi;
        int movies = moves;
        List<GameBoardSpace> allSpaces = gameBoard.getSpaces(); //list of all spaces

        List<Long> listi = new ArrayList<>(); //list of spaceIds that player moves over
        String color = null; //color of next space

        GameBoardSpace currentSpace = new GameBoardSpace(); //space currently on
        List<String> nextSpaceIds; //list of next spaces of space currently on

        while (movies > 0) {
            currentSpace = findSpaceById(allSpaces, currPosi);
            nextSpaceIds = currentSpace.getNext();

            // if next space is linear (not junction/gate)
            if (nextSpaceIds.size() == 1) {
                Long nextPosi = Long.parseLong(nextSpaceIds.get(0));
                listi.add(nextPosi);

                GameBoardSpace nextSpace = findSpaceById(allSpaces, nextPosi); // space next on
                color = nextSpace.getColor();

                //set player to next posi already
                currPosi = nextPosi;
                player.setPosition(currPosi);

                // check if game is over, or player gets cash
                if ("BlueGoal".equals(color) && nextSpace.getIsGoal()) {
                    if (player.getCanWin()) {
                        // TODO GAME OVER
                        return toReturn(player, listi, moves, color);
                    }
                    player.setCash(player.getCash() + 15);
                }

                // "partial end of walk", check on what decision can be done.
                // walk can never end in these space types btw (junction, gate, specialItem)
                if (nextSpace.getOnSpace() == null) {
                    if (color.equals("Junction")){
                        // return the currently walked spaces, after getting a response, continue with the walk
                        //TODO: BUT HOW TO HANDLE RESPONSE OF JUNCTION
                        // like handled in SpaceEffects
                        return toReturn(player, listi, moves, color);
                    }
                    else if (color.equals("Gate")){
                        //check if brother exists
                        // - if yes then return the currently walked spaces (like junction)
                        //TODO: BUT HOW TO HANDLE RESPONSE OF GATE
                        // like handled in SpaceEffects
                        for (Item item : player.getItems()){
                            if (item.getItemName().equals("TheBrotherAndCo")){
                                System.out.println("nono");
                                return toReturn(player,listi,moves,color);
                            }
                        }
                        // - if no then not even ask, just continue
                        // prob handle with recursion instead of just continuing
                        // TODO ugly ass Gate handling
                        currentSpace = findSpaceById(allSpaces, currPosi);
                        nextSpaceIds = currentSpace.getNext();
                        nextPosi = Long.parseLong(nextSpaceIds.get(0));
                        listi.add(nextPosi);
                        nextSpace = findSpaceById(allSpaces, nextPosi);
                        color = nextSpace.getColor();
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
        //TODO THE SPACE EFFECT NOW/LATER??
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

    private void turn(){}


    private void run(){}
    private void setup(){}
    private void teardown(){}
    private void flow(){}


    /*
    TODO SETUP
    Player kreieren
    Teams assigne
    Spieler reihefolg
    ultimate wählen
    win condition wählen
    Board laden/screen lade

    TODO TURNS
    items/ultimates
    update to player positions
    updates to player status
    würfeln/cards
    Updates to player positions
    updates to player status
    give turn to next player

    TODO ENDGAME
    update userprofiles
    remove players
    destroy gameboard
    display winners

    TODO ALWAYS
    display cash
    voice chat
    display items
    display cards
    display other players
    display win/ultimate (only for self)

     */


}
