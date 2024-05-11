package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import ch.uzh.ifi.hase.soprafs24.entity.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Ultimates and what they do:
 * - freshStart: All other Players get thrown back to their starting field.
 * - pickpocket: Steal half of the money of each player including your teammate.
 */
public class Ultimate { //NOSONAR

    public static List<String> getAllUltims(){
        List<String> allUltis = new ArrayList<>();
        allUltis.add("PickPocket");
        allUltis.add("FreshStart");
        allUltis.add("Wisdom");
        allUltis.add("Nothing");
        return allUltis;
    }

    public static String getRandomUltis(Long id, Game game) {
        List<String> allUltimates = game.getListOfAllUltis();
        return allUltimates.get(id.intValue()-1);
    }
}
