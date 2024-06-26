package ch.uzh.ifi.hase.soprafs24.entity;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.*;

public class GameBoardLoader {

    private static final Logger log = LoggerFactory.getLogger(GameBoardLoader.class);
    private static final String FILE_PATH = "src/main/java/ch/uzh/ifi/hase/soprafs24/entity/burger.json";

    private GameBoardLoader() {
        /*
         * Empty constructor for GameBoardLoader.
         * This constructor is intentionally left empty.
         * // No need of a constructor.
         */
    }

    public static List<GameBoardSpace> createGameBoardSpacesFromFile() {
        List<GameBoardSpace> gameBoardSpaces = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Map<String, Map<String, Object>>> topLevelData = mapper.readValue(new File(FILE_PATH),
                    new TypeReference<>() {});

            // Iterating through each space type ("hybridSpace", etc.)
            for (Map.Entry<String, Map<String, Map<String, Object>>> spaceTypeEntry : topLevelData.entrySet()) {
                // Iterating through each space within this type
                for (Map.Entry<String, Map<String, Object>> spaceEntry : spaceTypeEntry.getValue().entrySet()) {
                    // Extracting the spaceId from the key of the JSON structure
                    Long spaceId = Long.parseLong(spaceEntry.getKey());
                    // Assuming here that you just need to set the spaceId as is from the JSON key
                    GameBoardSpace space = mapper.convertValue(spaceEntry.getValue(), GameBoardSpace.class);
                    space.setSpaceId(spaceId);
                    gameBoardSpaces.add(space);
                }
            }
        } catch (Exception e) {
            log.error("this error ", e);
        }
        return gameBoardSpaces;
    }
}
