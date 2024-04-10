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
            Map<String, Map<String, Map<String, Object>>> topLevelData = mapper.readValue(new File(FILE_PATH),
                    new TypeReference<>() {});

            // Iterating through each space type ("hybridSpace", etc.)
            for (Map.Entry<String, Map<String, Map<String, Object>>> spaceTypeEntry : topLevelData.entrySet()) {
                // String spaceType = spaceTypeEntry.getKey(); // If you need to use the space type

                // Iterating through each space within this type
                for (Map.Entry<String, Map<String, Object>> spaceEntry : spaceTypeEntry.getValue().entrySet()) {
                    // Extracting the spaceId from the key of the JSON structure
                    Long spaceId = Long.parseLong(spaceEntry.getKey());

                    // Applying (spaceId % 63) + 1 logic if needed right here or just setting the spaceId directly
                    // Assuming here that you just need to set the spaceId as is from the JSON key
                    GameBoardSpace space = mapper.convertValue(spaceEntry.getValue(), GameBoardSpace.class);
                    space.setSpaceId(spaceId); // Set the spaceId extracted from the key

                    gameBoardSpaces.add(space);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gameBoardSpaces;
    }
}
