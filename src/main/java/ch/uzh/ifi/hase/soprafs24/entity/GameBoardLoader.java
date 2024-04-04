package ch.uzh.ifi.hase.soprafs24.entity;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.util.*;

public class GameBoardLoader {

    private static final String FILE_PATH = "src/main/java/ch/uzh/ifi/hase/soprafs24/entity/burger.json";

    public static List<GameBoardSpace> createGameBoardSpacesFromFile() {
        List<GameBoardSpace> gameBoardSpaces = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Read the file and convert it into a map
            Map<String, Map<String, Object>> data = mapper.readValue(new File(FILE_PATH),
                    new TypeReference<Map<String, Map<String, Object>>>() {});

            // For each key in the map, create a GameBoardSpace object
            for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
                // Use Jackson's convertValue to map the inner map to a GameBoardSpace object
                GameBoardSpace space = mapper.convertValue(entry.getValue(), GameBoardSpace.class);
                gameBoardSpaces.add(space);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameBoardSpaces;
    }
}
