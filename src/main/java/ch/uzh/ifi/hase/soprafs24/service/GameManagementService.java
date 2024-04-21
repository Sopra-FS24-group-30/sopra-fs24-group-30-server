package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;
import ch.uzh.ifi.hase.soprafs24.entity.AchievementStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
@Service
public class GameManagementService {

    private ConcurrentHashMap<Long, Game> allGames = new ConcurrentHashMap<>();

    public Map<String, String> manualParse(String message){
        message = message.trim();
        message = message.replaceAll("[{}\"\\\\]", "");
        String[] pairs = message.split(",");
        Map<String, String> dict = new HashMap<>();

        for (String pair: pairs){
            String[] parts = pair.split(":");
            if (parts.length != 2){
                throw new IllegalStateException("You're message cannot be accepted");
            }
            String key = parts[0].trim();
            String value = parts[1].trim();
            dict.put(key, value);
        }
        return dict;
    }

    /**
     * Creates a new game with a random game ID.
     * If the game already exists, this method does nothing.
     * @param gameId the unique identifier for the new game
     */
    public Long createGameId(){
        Random random = new Random();
        long id;
        do{
            id = 100000 + random.nextInt(900000);
        } while(allGames.containsKey(id));
        return id;
    }
    public Long createGame(String playerId) {
        Long gameId = createGameId();
        Game game = new Game();
        game.setId(gameId);
        game.setStatus(GameStatus.NOT_PLAYING);

        List<String> playerList = new ArrayList<>();
        playerList.add(playerId);
        game.setPlayers(playerList);

        System.out.println(gameId);
        System.out.println(playerList);

        allGames.put(gameId, game);
        return gameId;
    }

    /**
     * Attempts to add a client to a specified game.
     * @param gameId the game to join
     * @param sessionId the session ID of the client joining the game
     * @return true if the client was added successfully, false if the game is full or does not exist
     */

    private Game findGame(Long gameId){
        Game game = allGames.get(gameId);
        if (game == null){
            throw new IllegalArgumentException("Game not found");
        }
        return game;
    }

    public boolean joinGame(Long gameId, String playerId) {
        Game game = findGame(gameId);
        System.out.println(gameId);
        System.out.println(playerId);
        if (game == null){
            throw new IllegalStateException("Game does not exist");
        }
        if (game.getPlayers().size() >= 4){
            throw new IllegalStateException("Cannot add more players to the game");
        }
        if (game.getPlayers().contains(playerId)){
            return true;
        }
        game.addPlayer(playerId);

        System.out.println(gameId);
        System.out.println(game.getPlayers());
        return true;
    }

    /**
     * Retrieves a list of all playerIds in a specific room.
     * @param roomId the room ID
     * @return a list of session IDs or an empty list if no room exists
     */
    public List<String> getPlayersInGame(Long gameId) {
        Game game = findGame(gameId);
        return game.getPlayers();
    }

    /**
     * Removes a client from a game. This is typically called when a client disconnects.
     * @param sessionId the session ID of the client to remove
     */
    public void leaveGame(Long gameId, String playerId) {
        Game game = findGame(gameId);
        game.removePlayer(playerId);
        removeGameIfEmpty(gameId);
    }

    /**
     * Checks if a game is empty and removes it if so.
     * This method can be called after a player leaves to clean up empty games.
     * @param gameId the game ID to check
     */
    public void removeGameIfEmpty(Long gameId) {
        Game game = findGame(gameId);
        if (game != null && game.getPlayers().isEmpty()) {
            allGames.remove(gameId);
        }
    }

    /**
     * Gets all the players, who are in the game
     * Used to get all the players and display it in the front end
     * @param gameId the game ID to check
     */
    public List<String> lobbyPlayers(Long gameId) {
        List<String> plrs = getPlayersInGame(gameId);
        return plrs;
    }

    /**
     * Sets the gamestatus to setUp
     * Used to change the status of the game
     * @param gameId the game ID to check
     */
    public boolean changeGameStatus(Long gameId, GameStatus status){
        Game game = findGame(gameId);
        game.setStatus(status);
        if (game.getStatus() != status) {
            throw new IllegalStateException("Game status couldn't be changed");
        }
        return true;
    }

    public GameStatus getGameStatus(Long gameId){
        Game game = findGame(gameId);
        return game.getStatus();
    }
}