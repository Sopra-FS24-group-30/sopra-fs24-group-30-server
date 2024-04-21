package ch.uzh.ifi.hase.soprafs24.logic.Game; //NOSONAR

import java.util.HashMap;
import java.util.Map;

/**
 * Lookup Tables for any spaces
 */
public class SpaceEffects { //NOSONAR
    private static final Map<String, String> spaces;

    static{
        spaces = new HashMap<>();
        spaces.put("1", "goal");
        spaces.put("2", "blue");
        spaces.put("3", "item"); //see Item class for more details
        spaces.put("4", "card"); //see Card class for more details
        spaces.put("5", "gambling");
        spaces.put("6", "catnami"); //see CatNami class for more details
        spaces.put("7", "black"); //see BlackBigOops class for more details
        spaces.put("8", "red"); //see RedSmallOops class for more details
        spaces.put("9", "junction");
        spaces.put("10", "gate");
        spaces.put("11", "specialItem");
        spaces.put("12", "teleportToSpace49");
        spaces.put("13", "teleportToSpace13");
        spaces.put("14", "teleportToAStart");
        spaces.put("18", "steal10OthersMoney");
        spaces.put("19", "nothingLOL");
        spaces.put("23", "gift10YourMoney");
        spaces.put("26", "surpriseMF");
        spaces.put("28", "spawn");
    }

    public static String getSpaceEffectValue(String key) {
        return spaces.get(key);
    }

    public static void junction(Player player){} //NOSONAR

    public static void gate(Player player){} //NOSONAR

    public static void specialItem(Player player){
        int rando = (int) (Math.random() * Item.getAllItems().size()); //NOSONAR
        player.addItem(Item.getAllItems().get(rando));
    }

}
