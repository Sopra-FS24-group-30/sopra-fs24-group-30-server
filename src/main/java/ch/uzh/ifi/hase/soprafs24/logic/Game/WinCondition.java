package ch.uzh.ifi.hase.soprafs24.logic.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * At the start of a game the player gets one of the WinConditions randomly.
 * WinConditions and what they do:
 * - JackSparrow: You win if the other Team wins, and you lose if your Partner wins. If the game ends after 20 Turns, everyone except for your Partner loses.
 * - theMarooned: As long as you have exactly 0 Moneys, 0 Items and 0 Cards the Win Condition is fulfilled.
 * - goldenIsMy...: Land on seven golden spaces.
 * - drunk: Land on a tsunami Space thrice.
 */
public class WinCondition {
    private String winConditionName;

    public WinCondition(String winConditionName) {
        this.winConditionName = winConditionName;
    }

    public String getWinConditionName(){
        return winConditionName;
    }

    public static List<WinCondition> getAllWinConditions(){
        List<WinCondition> allWinConditions = new ArrayList<>();
        allWinConditions.add(new WinCondition("JackSparrow"));
        allWinConditions.add(new WinCondition("theMarooned"));
        allWinConditions.add(new WinCondition("goldenIsMy..."));
        allWinConditions.add(new WinCondition("drunk"));
        return getAllWinConditions();
    }

    public boolean checkWinConditionMet(Player player){
        switch (winConditionName){
            case "JackSparrow":
                //TODO HOW TF???
                //how to gameover
            case "theMarooned":
                return player.getCash()==0 && player.getCards().isEmpty() && player.getItems().isEmpty();
            case "goldenIsMy...":
                return player.getLandYellow() >= 7;
            case "drunk":
                return player.getLandCat() >= 3;
            default:
                return false;
        }
    }
}
