package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Game;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameManagementServiceTest {

    @Autowired
    private GameManagementService gameManagementService;

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
}