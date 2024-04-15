package ch.uzh.ifi.hase.soprafs24.gamelogic;

import java.util.HashMap;
import java.util.Map;

/**
 * Lookup Tables for any spaces
 */
public class SpaceEffects {
    private static final Map<Integer, String> spaces;

    static{
        spaces = new HashMap<>();
        spaces.put(1, "goal");
        spaces.put(2, "blue");
        spaces.put(3, "item");
        spaces.put(4, "card");
        spaces.put(5, "gambling");
        spaces.put(6, "catnami");
        spaces.put(7, "black");
        spaces.put(8, "red");
        spaces.put(9, "junction");
        spaces.put(10, "gate");
        spaces.put(11, "specialItem");
        spaces.put(12, "teleportToSpace49");
        spaces.put(13, "teleportToSpace13");
        spaces.put(14, "teleportToAStart");
        spaces.put(18, "steal10OthersMoney");
        spaces.put(19, "nothingLOL");
        spaces.put(23, "gift10YourMoney");
        spaces.put(26, "surpriseMF");
        spaces.put(28, "spawn");
    }

    public static String getSpaceEffectValue(int key) {
        return spaces.get(key);
    }
}
