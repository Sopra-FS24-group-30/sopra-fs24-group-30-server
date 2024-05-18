package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;


import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Test
    public void test_createPlayerForGame_correct() {
        User user = new User();
        Game game = new Game();
        user.setUsername("test");

        Player player = gameService.createPlayerForGame(user, 0, game);

        assertEquals((long) 1, player.getPlayerId());
        assertEquals(user, player.getUser());
        assertEquals(user.getUsername(), player.getPlayerName());
        assertEquals(15, player.getCash());
        assertTrue(player.getWinCondition()!=null);
        //TODO: assertTrue(player.getUltimateAttack());
    }

    @Test
    public void test_createPlayerForGame_TooManyPlayers(){
        User user = new User();
        Game game = new Game();
        user.setUsername("test");

        IllegalStateException exception = assertThrows(IllegalStateException.class, ()->gameService.createPlayerForGame(user, 4, game), "Expected createPlayerForGame to throw but it didn't");
        assertEquals("Cannot add more players to the game. The game is full.", exception.getMessage());
    }
}