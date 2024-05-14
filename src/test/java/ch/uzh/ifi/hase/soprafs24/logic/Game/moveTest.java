import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardLoader;
import ch.uzh.ifi.hase.soprafs24.service.GameBoardService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
public class moveTest {
    private GameFlow gameFlow;
    private GameBoard gameBoard;
    private Player player;
    private GameBoardSpace spaceOne, spaceTwo;

    @BeforeEach
    public void setup() {
        gameFlow = new GameFlow();
        gameBoard = mock(GameBoard.class);
        spaceOne = mock(GameBoardSpace.class);
        spaceTwo = mock(GameBoardSpace.class);

        // Setup mock responses
        when(gameBoard.getSpaces()).thenReturn(List.of(spaceOne, spaceTwo));
        when(spaceOne.getSpaceId()).thenReturn(24L);  // Start space
        when(spaceOne.getColor()).thenReturn("Start");

        when(spaceTwo.getSpaceId()).thenReturn(19L);  // Target space
        when(spaceTwo.getColor()).thenReturn("Blue");

        // Setup player
        player = new Player();
        player.setPlayerId(1L);
        player.setPosition(spaceOne.getSpaceId()); // Set the initial position of the player

    }

    @Test
    public void testPlayerMovesThroughSpaces() {
        // Create a game board with spaces loaded from GameBoardLoader
        gameFlow = new GameFlow();
        List<GameBoardSpace> spaces = GameBoardLoader.createGameBoardSpacesFromFile();
        GameBoard gameBoard = new GameBoard();
        gameBoard.setSpaces(spaces);

        // Prepare the player and set an initial position, assuming players start at position 53 or 54

        // Simulate the movement logic
        long currentPosition = player.getPosition(); // Current position is retrieved from the player
        Map<String, Object> result = gameFlow.move(5, currentPosition);

        // Verify the expected behavior
        assertNotNull(result);
        assertTrue(result.containsKey(player.getPlayerId().toString())); // Player ID should be a key in the result map
        Map<String, Object> playerData = (Map<String, Object>) result.get(player.getPlayerId().toString());
        assertNotNull(playerData.get("spaces"));
        assertTrue(((List<Long>)playerData.get("spaces")).contains(19L)); // Check that the space moved to is 19
        assertEquals("Blue", playerData.get("spaceColor")); // The color of the final space the player landed on
        assertEquals(5, playerData.get("moves")); // Number of moves made should be 5
    }

}