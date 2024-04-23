package ch.uzh.ifi.hase.soprafs24.logic.Game.ItemEffects;
import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;

public class Getem {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("./src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/ItemEffects/items.json"));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        // delete the last new line separator
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();


        System.out.println(stringBuilder);
    }

}

