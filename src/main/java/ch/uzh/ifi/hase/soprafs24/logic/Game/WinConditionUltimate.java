package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import java.util.ArrayList;
import java.util.List;

public class WinConditionUltimate { //NOSONAR

    public static List<String> getAllWinConditions(){
        List<String> allWinConditions = new ArrayList<>();
        allWinConditions.add("JackSparrow");
        allWinConditions.add("Marooned");
        allWinConditions.add("Golden");
        allWinConditions.add("Drunk");
//        allWinConditions.add("ThirdTime");
        allWinConditions.add("Company");
        allWinConditions.add("Ship");
        allWinConditions.add("Explorer");
        allWinConditions.add("Unlucky");
        return allWinConditions;
    }

    public static boolean checkWinConditionMet(Player player){
        return switch (player.getWinCondition()) {
            case "JackSparrow" -> false; //NOSONAR
            case "Marooned" ->
                    player.getCash() == 0 && player.getCardNames().isEmpty() && player.getItemNames().isEmpty();
            case "Golden" -> player.getLandYellow() >= 7;
            case "Drunk" -> player.getLandCat() >= 3;
            case "ThirdTime" ->  player.getPassGoal() >= 2;
            case "Company" -> player.getCash() >= 60;
            case "Ship" -> player.getShipAct()>=1;
            case "Explorer" -> player.getLandedAll().size() >= 61;
            case "Unlucky" -> player.getLostCash() >= 40;
            default -> false;
        };
    }

    public static String getRandomWinCondition(Long id, Game game) {
        List<String> allWinConditions = game.getListOfAllCondition();
        return allWinConditions.get(id.intValue()-1);
    }

    public static String getRandomUltimate(Long id, Game game) {
        List<String> allUltimates = game.getListOfAllUltis();
        return allUltimates.get(id.intValue()-1);
    }
}
