package ch.uzh.ifi.hase.soprafs24.logic.Game;

import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;

import java.util.List;

public class GameFlow {

    private void createBoard(){}

    private void getWinCondition(){}

    private void getUltimate(){}

    private void addPlayer(){}

    private void useItem(){}
    private void useCard(){}

    public int throwDice(){
        return (int) Math.floor(Math.random() * 6 + 1);
    }

    private void updatePlayer(){}

    public void updatePlayerPosition(GameBoard gameBoard, Player player, int moves){
        Long currPosi = player.getPosition();
        //currPosi = 42L;
        List<GameBoardSpace> spacies = gameBoard.getSpaces();
        int total = spacies.size();
        while (moves > 0) {
            List<String> nexi = null;
            String nextSpaceColor = null;
            //TODO: replace for loop with smth to find space by Id directly from list
            for (int i = 0; i < total; i++) {
                GameBoardSpace unoSpace = spacies.get(i);
                if (unoSpace.getSpaceId().equals(currPosi)) {
                    nexi = unoSpace.getNext();

                    // get Color of next space, look if next space is a junction
                    int juncti = Integer.parseInt(nexi.get(0));
                    nextSpaceColor = spacies.get(juncti-1).getColor();

                    break;
                }
            }

            if (nexi.size() == 1 && !nextSpaceColor.equals("Junction") && !nextSpaceColor.equals("Gate") && !nextSpaceColor.equals("SpecialItem")){
                Long newposi = Long.parseLong(nexi.get(0));
                player.setPosition(newposi);
                currPosi = newposi;
                moves--;
            }
            else if (nexi.size() == 1){
                Long newposi = Long.parseLong(nexi.get(0));
                player.setPosition(newposi);
                currPosi = newposi;
            }
            else {
                //TODO: junctions how to
                //currently taking the first next of list
                Long newposi = Long.parseLong(nexi.get(0));
                player.setPosition(newposi);
                currPosi = newposi;
                moves--;
            }
        }
        System.out.println(String.format("effect for space %d", currPosi));
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
