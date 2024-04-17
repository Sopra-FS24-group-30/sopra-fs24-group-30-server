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

    private void updatePlayer(){}

    public void updatePlayerPosition(GameBoard gameBoard, Player player, int moves) {
        Long currPosi = player.getPosition();
        //currPosi = 15L; //dummyPosi
        List<GameBoardSpace> allSpaces = gameBoard.getSpaces();
        List<Long> listi = new ArrayList<>();
        String color = null;
        int movies = moves;
        while (moves > 0) {
            GameBoardSpace currentSpace = findSpaceById(allSpaces, currPosi);

            List<String> nextSpaceIds = currentSpace.getNext();

            if (nextSpaceIds.size() == 1) {
                String nextSpaceId = nextSpaceIds.get(0);
                Long nextPosi = Long.parseLong(nextSpaceId);
                listi.add(nextPosi);
                player.setPosition(nextPosi);
                currPosi = nextPosi;
                GameBoardSpace nextSpace = findSpaceById(allSpaces, nextPosi);
                color = nextSpace.getColor();

                if (nextSpace.getOnSpace() == null) {
                    break;

                    //send data to friend (via websocket)
                    //recursively call this functiön
                }
                moves--;
            }
        }
        Map<String, Object> dici = new HashMap<>();
        dici.put("spaces", listi);
        dici.put("moves", movies);
        dici.put("spaceColor", color);
        // send dici to frontend
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
