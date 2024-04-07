package ch.uzh.ifi.hase.soprafs24.entity;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.util.*;

public class GameBoardLoader {

    private static final String FILE_PATH = "src/main/java/ch/uzh/ifi/hase/soprafs24/entity/burger.json";
    // Adjusted path for resources

    public static List<GameBoardSpace> createGameBoardSpacesFromFile() {
        List<GameBoardSpace> gameBoardSpaces = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Adjusted to reflect the actual nested structure of your JSON
            Map<String, Map<String, Map<String, Object>>> topLevelData = mapper.readValue(new File(FILE_PATH),
                    new TypeReference<>() {});

            // Iterating through each space type ("hybridSpace", etc.)
            for (Map.Entry<String, Map<String, Map<String, Object>>> spaceTypeEntry : topLevelData.entrySet()) {
                String spaceType = spaceTypeEntry.getKey(); // Not directly used in this snippet, but you might want to

                // Iterating through each space within this type
                for (Map.Entry<String, Map<String, Object>> spaceEntry : spaceTypeEntry.getValue().entrySet()) {
                    GameBoardSpace space = mapper.convertValue(spaceEntry.getValue(), GameBoardSpace.class);
                    // Here you could set the spaceType or any other properties if needed
                    gameBoardSpaces.add(space);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gameBoardSpaces;
    }
}
