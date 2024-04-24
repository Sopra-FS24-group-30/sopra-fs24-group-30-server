package ch.uzh.ifi.hase.soprafs24.logic.Game.ItemEffects;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Getem {

    public static void main(String[] args){
        getItems();
    }

    public static HashMap<String, ArrayList<String>> getItems(){
        String jsonData = null;
        try{
            jsonData = getJson();
        }catch (IOException e){
            throw new RuntimeException("the json object could not be created");
        }
        JSONObject obj = new JSONObject(jsonData);
        //String pageName = obj.getJSONObject("pageInfo").getString("pageName");

        HashMap<String, ArrayList<String>> ret = new HashMap<String, ArrayList<String>>();
        ArrayList<String> m = new ArrayList<>();
        m.add("1");
        ret.put("1",m);
        return ret;
    }

    private static String getJson() throws IOException {

        try{
            BufferedReader reader = new BufferedReader(new FileReader("./src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/ItemEffects/items.json"));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            String ls = System.lineSeparator();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            // delete the last new line separator
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();

            return stringBuilder.toString();
        }catch(IOException e){
            throw new IOException("error while parsing file");
        }

    }

}

