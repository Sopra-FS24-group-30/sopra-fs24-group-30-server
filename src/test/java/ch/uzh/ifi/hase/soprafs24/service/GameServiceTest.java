package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.logic.Game.AchievementProgress;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;


import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Test
    public void test_createPlayerForGame_correct() {
        User user = new User();
        user.setUsername("test");

        Player player = gameService.createPlayerForGame(user, 0);
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
        user.setUsername("test");

        IllegalStateException exception = assertThrows(IllegalStateException.class, ()->gameService.createPlayerForGame(user, 4), "Expected createPlayerForGame to throw but it didn't");
        assertEquals("Cannot add more players to the game. The game is full.", exception.getMessage());
    }
}