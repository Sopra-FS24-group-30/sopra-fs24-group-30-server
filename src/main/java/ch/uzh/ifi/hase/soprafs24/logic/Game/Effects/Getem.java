package ch.uzh.ifi.hase.soprafs24.logic.Game.Effects;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Getem {


    public static HashMap<String, JSONObject> getItems(){
        String jsonData;
        try{
            jsonData = getJson("./src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/Effects/items.json");
        }catch (IOException e){
            throw new RuntimeException("the json object could not be created");
        }
        JSONObject jsonObject = new JSONObject(jsonData);

        HashMap<String, JSONObject> ret = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()){
            String key = keys.next();
            JSONObject effectComplete = jsonObject.getJSONObject(key);
            ret.put(key,effectComplete);
        }

        return ret;
    }

    public static HashMap<String, JSONObject> getUltimates(){
        HashMap<String, JSONObject> ultimates = new HashMap<>();

        String jsonData;
        try{
            jsonData = getJson("./src/main/java/ch/uzh/ifi/hase/soprafs24/logic/Game/Effects/ultimates.json");
        }catch (IOException e){
            throw new RuntimeException("the json object could not be created");
        }
        JSONObject jsonObject = new JSONObject(jsonData);

        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()){
            String key = keys.next();
            JSONObject effectComplete = jsonObject.getJSONObject(key);
            ultimates.put(key,effectComplete);
        }

        return ultimates;
    }

    private static String getJson(String path) throws IOException {
        BufferedReader reader = null;
        try{
            //TODO: make try with here
            reader = new BufferedReader(new FileReader(path));
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
        }finally {
            if(reader != null){
                reader.close();
            }
        }

    }

}

