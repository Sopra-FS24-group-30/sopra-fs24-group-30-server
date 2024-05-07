package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.controller.GameWebSocketController;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

@Service
public class GameManagementService {

    private static ConcurrentHashMap<Long, Game> allGames = new ConcurrentHashMap<>();

    private static GameService gameService; // Final field for the injected service

    @Autowired // Optional if it's the only constructor, Spring will use it by default
    public GameManagementService(GameService gameeService) {
        gameService = gameeService; // Assigning the injected service
    }

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
    public static Long createGameId(){
        Random random = new Random();
        long id;
        do{
            id = 100000 + random.nextInt(900000);
        } while(allGames.containsKey(id));
        return id;
    }
    public static Long createGame(String playerId) {
        Long gameId = createGameId();
        Game game = gameService.setUpGame();
        game.setId(gameId);
        game.setStatus(GameStatus.NOT_PLAYING);

        List<String> playerList = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        playerList.add(playerId);
        game.setPlayers(playerList);
        game.setactive_players(players);
        System.out.println("These are the active players before:");
        System.out.println(game.getactive_Players());

        allGames.put(gameId, game);

        //when creating the came, saved in GameWebSocketController, for further usage there
        GameWebSocketController.addGame(gameId,allGames.get(gameId));

        return gameId;
    }

    /**
     * Attempts to add a client to a specified game.
     * @param gameId the game to join
     * @param sessionId the session ID of the client joining the game
     * @return true if the client was added successfully, false if the game is full or does not exist
     */

    public static Game findGame(Long gameId){
        Game game = allGames.get(gameId);
        if (game == null){
            throw new IllegalArgumentException("Game not found");
        }
        return game;
    }

    public boolean joinGame(Long gameId, String playerId) {
        // change
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
        game.addPlayer(playerId); // add player to the game

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
        Game game = findGame(gameId); // List of Players
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
        return getPlayersInGame(gameId);
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