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
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;

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
public class GameManagementServiceTest {

    @Autowired
    private GameManagementService gameManagementService;

    private static ConcurrentHashMap<Long, Game> allGames;

    @Mock
    private GameRepository gameRepository;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);

        try {
            Field allGamesField = GameManagementService.class.getDeclaredField("allGames");
            allGamesField.setAccessible(true);

            allGames = new ConcurrentHashMap<>();
            allGamesField.set(null, allGames);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Game createGameWithPlayers() {
        Game game = new Game();
        game.setId(1L);

        List<Player> playerList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Player player = new Player();
            player.setPlayerId((long) i);
            player.setPlayerName("player " + i);
            playerList.add(player);
        }
        game.setactive_players(playerList);

        allGames.put(game.getId(), game);  // Make sure the game is in the map

        return game;
    }

    @Test
    public void testSetTeams_sorted(){
        Game game = new Game();
        long i = 1;
        game.setId(i);

        Player player1 = new Player();
        player1.setPlayerName("player 1");
        Player player2 = new Player();
        player2.setPlayerName("player 2");
        Player player3 = new Player();
        player3.setPlayerName("player 3");
        Player player4 = new Player();
        player4.setPlayerName("player 4");

        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        game.setactive_players(playerList);

        gameManagementService.setTeams(game, "player 1", "player 4");

        assertEquals("player 1", game.getactive_Players().get(0).getPlayerName());
        assertEquals("player 2", game.getactive_Players().get(1).getPlayerName());
        assertEquals("player 4", game.getactive_Players().get(2).getPlayerName());
        assertEquals("player 3", game.getactive_Players().get(3).getPlayerName());
    }

    @Test
    public void testSetTeams_correctId(){
        Game game = new Game();
        long i = 1;
        game.setId(i);

        Player player1 = new Player();
        player1.setPlayerName("player 1");
        Player player2 = new Player();
        player2.setPlayerName("player 2");
        Player player3 = new Player();
        player3.setPlayerName("player 3");
        Player player4 = new Player();
        player4.setPlayerName("player 4");

        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        game.setactive_players(playerList);

        gameManagementService.setTeams(game, "player 1", "player 4");

        assertEquals(1, game.getactive_Players().get(0).getPlayerId());
        assertEquals(2, game.getactive_Players().get(1).getPlayerId());
        assertEquals(3, game.getactive_Players().get(2).getPlayerId());
        assertEquals(4, game.getactive_Players().get(3).getPlayerId());
    }

    @Test
    public void testSetTeams_correctTeamMates(){
        Game game = new Game();
        long i = 1;
        game.setId(i);

        Player player1 = new Player();
        player1.setPlayerName("player 1");
        Player player2 = new Player();
        player2.setPlayerName("player 2");
        Player player3 = new Player();
        player3.setPlayerName("player 3");
        Player player4 = new Player();
        player4.setPlayerName("player 4");

        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        game.setactive_players(playerList);

        gameManagementService.setTeams(game, "player 1", "player 4");

        assertEquals(3, game.getactive_Players().get(0).getTeammateId());
        assertEquals(4, game.getactive_Players().get(1).getTeammateId());
        assertEquals(1, game.getactive_Players().get(2).getTeammateId());
        assertEquals(2, game.getactive_Players().get(3).getTeammateId());
    }

    @Test
    void test_getUsables(){
        Player player = new Player();

        ArrayList<String> items = new ArrayList<>();
        items.add("usable1");

        ArrayList<String> cards = new ArrayList<>();
        cards.add("usable2");

        player.setItemNames(items);
        player.setCardNames(cards);

        player.addItemNames("usable3");
        player.addCardNames("usable4");

        List<String> usables = new ArrayList<>();
        usables.add("usable1");
        usables.add("usable3");
        usables.add("usable2");
        usables.add("usable4");

        assertEquals(usables, gameManagementService.getUsables(player));
    }

    @Test
    void test_getInformationPlayers(){
        Game game = new Game();
        game.setId(1L);

        List<Player> playerList = new ArrayList<>();

        for(int i=1; i<5; i++){
            Player player = new Player();
            player.setAchievementProgress(new AchievementProgress((long) i));
            player.setPlayerId((long) i);
            player.setCash(15);
            playerList.add(player);
        }

        game.setactive_players(playerList);
        allGames.put(1L, game);

        Map<String, Object> results = gameManagementService.getInformationPlayers(game.getId());
        System.out.println(results);

        assertEquals(4, results.size(), "There should be information for four players");

        for(Map.Entry<String, Object> entry: results.entrySet()){
            Map<String, Object> playerInfo = (Map<String, Object>) entry.getValue();
            assertEquals(15, playerInfo.get("cash"));
        }
    }

    @Test
    void test_changePlayerStatus(){
        Game game = createGameWithPlayers();
        allGames.put(1L, game);

        Player player1 = game.getactive_Players().get(0);
        player1.setStatus(PlayerStatus.READY);

        gameManagementService.changePlayerStatus(game.getId(), "player 1", PlayerStatus.READY);

        assertEquals(PlayerStatus.READY, player1.getStatus());
    }

    @Test
    void test_setGameReady(){
        Game game = createGameWithPlayers();
        allGames.put(1L, game);

        List<Player> playerList = game.getactive_Players();
        for(Player player: playerList){
            gameManagementService.changePlayerStatus(game.getId(), player.getPlayerName(), PlayerStatus.READY);
        }
        gameManagementService.setGameReady(game.getId());

        assertEquals(GameStatus.READY, game.getStatus());
    }
}