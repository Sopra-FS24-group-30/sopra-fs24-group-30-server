package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.entity.Game;
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

    public static List<String> getAllWinConditions(){
        List<String> allWinConditions = new ArrayList<>();
        allWinConditions.add("JackSparrow");
        allWinConditions.add("Marooned");
        allWinConditions.add("Golden");
        allWinConditions.add("Drunk");
        return allWinConditions;
    }

    public static boolean checkWinConditionMet(Player player){
        return switch (player.getWinCondition()) {
            case "JackSparrow" -> false; //NOSONAR
            case "Marooned" ->
                    player.getCash() == 0 && player.getCardNames().isEmpty() && player.getItemNames().isEmpty();
            case "Golden" -> player.getLandYellow() >= 7;
            case "Drunk" -> player.getLandCat() >= 3;
            default -> false;
        };
    }

    public static String getRandomWinCondition(Long id, Game game) {
        List<String> allWinConditions = game.getListOfAllCondition();
        return allWinConditions.get(id.intValue()-1);
    }
}
