package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.controller.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameManagementService {

    private static final Random random = new Random();
    private static ConcurrentHashMap<Long, Game> allGames = new ConcurrentHashMap<>();

    private static GameService gameService; // Final field for the injected service
    private static UserService userService;

    @Autowired // Optional if it's the only constructor, Spring will use it by default
    public GameManagementService(GameService gameService, UserService userService) {
        this.gameService = gameService; // Assigning the injected service
        this.userService = userService;
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
        long id;
        do{
            id = 100000 + random.nextInt(900000);
        } while(allGames.containsKey(id));
        return id;
    }

    public static Long createGame(String userId) {
        Long gameId = createGameId();
        Game game = gameService.setUpGame();
        game.setId(gameId);
        game.setStatus(GameStatus.NOT_PLAYING);

        List<String> playerList = new ArrayList<>();
        List<Player> players = new ArrayList<>();

        playerList.add(userId);
        User user = userService.findUserWithId(Long.valueOf(userId));
        Collections.shuffle(game.getListOfAllCondition());
        Collections.shuffle(game.getListOfAllUltis());
        Player player = gameService.createPlayerForGame(user, 0, game);

        players.add(player);
        game.setPlayers(playerList);
        game.setactive_players(players);
        System.out.println("These are the active players before:");
        System.out.println(game.getactive_Players());

        allGames.put(gameId, game);

        //when creating the came, saved in GameWebSocketController, for further usage there
        GameWebSocketController.addGame(gameId,allGames.get(gameId));
        //GameWebSocketController.setCurrGame(allGames.get(gameId));
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

    public Player findPlayerById(Game game, Long playerID) {
        for (Player player : game.getactive_Players()) {
            System.out.println("Checking player ID: " + player.getPlayerId() + " against " + playerID);

            if (player.getPlayerId().equals(playerID)) {
                return player;
            }
        }
        return null; // Return null or throw an exception if the player is not found
    }

    public boolean joinGame(Long gameId, String userId) { //NOSONAR
        Game game = findGame(gameId);
        System.out.println(gameId);
        System.out.println(userId);
        System.out.println("all players (before adding): " + game.getPlayers());
        if (game == null){
            throw new IllegalStateException("Game does not exist");
        }else if ((game.getPlayers().size() >= 4) && (!game.getPlayers().contains(userId))){
            System.out.println("Exception gets thrown here");
            throw new IllegalStateException("Cannot add more players to the game");
        }else if (game.getPlayers().contains(userId)){
            return true;
        }
        User user = userService.findUserWithId(Long.valueOf(userId));
        Player player = gameService.createPlayerForGame(user, game.getPlayers().size(), game);
        game.addNEWPlayer(player);
        game.addPlayer(userId); // add player to the game

        System.out.println(gameId);
        System.out.println("all players (after adding)" + game.getPlayers());
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

    /**
     * Gets all the players in a game
     * Used to return all the players either for team selection
     * or for getting all the usesrs for the board
     * @param gameId the game ID to check
     */
    public List<Player> getActivePlayers(Long gameId){
        Game game = findGame(gameId);
        return game.getactive_Players();
    }

    public void setTeams(Game game, String player1, String player2) {
        Player firstPlayer = null;
        Player secondPlayer = null;

        System.out.println("Setting the teams");
        List<Player> playerList = game.getactive_Players();
        System.out.println("actual player list before parsing: " + playerList);
        for (Player player : playerList) {
            System.out.println(player.getPlayerName());
            if (player.getPlayerName().equals(player1)) {
                System.out.println("host" + player);
                firstPlayer = player;
            }
            else if (player.getPlayerName().equals(player2)) {
                System.out.println("teammate" + player);
                secondPlayer = player;
            }
        }

        playerList.remove(firstPlayer);
        playerList.remove(secondPlayer);
        System.out.println("after removing: "+ playerList);
        playerList.add(0, firstPlayer);
        playerList.add(2, secondPlayer);
        System.out.println("after adding: "+ playerList);

        for (int i = 0; i < 4; i++) {
            System.out.println(playerList.get(i));
            int j = i+1;
            playerList.get(i).setPlayerId((Long.valueOf(j)));
            System.out.println(playerList.get(i).getPlayerName());
        }

        playerList.get(0).setTeammateId(playerList.get(2).getPlayerId());
        playerList.get(1).setTeammateId(playerList.get(3).getPlayerId());
        playerList.get(2).setTeammateId(playerList.get(0).getPlayerId());
        playerList.get(3).setTeammateId(playerList.get(1).getPlayerId());
        playerList.get(0).setPosition(GameFlow.findStart(playerList.get(0).getPlayerId().intValue()));
        playerList.get(1).setPosition(GameFlow.findStart(playerList.get(1).getPlayerId().intValue()));
        playerList.get(2).setPosition(GameFlow.findStart(playerList.get(2).getPlayerId().intValue()));
        playerList.get(3).setPosition(GameFlow.findStart(playerList.get(3).getPlayerId().intValue()));
    }

    public List<String> getUsables(Player player){
        List<String> usables = player.getItemNames();
        if(player.getCardNames() != null){
            for (String card: player.getCardNames()){
                usables.add(card);
            }
        }
        return usables;
    }

    public Map<String, Object> getInformationPlayers(Long gameId){
        Game game = findGame(gameId);
        Map<String, Object> players = new HashMap();

        for (Player player: game.getactive_Players()){
            System.out.println(player.getPlayerName());
            Map<String, Object> dictionary = new HashMap<>();
            dictionary.put("userId", player.getUserId());
            dictionary.put("username", player.getPlayerName());
            dictionary.put("teammateId", player.getTeammateId());
            dictionary.put("cash", player.getCash());
            dictionary.put("usables", getUsables(player));

            players.put(Long.toString(player.getPlayerId()), dictionary);
        }

        System.out.println(players);
        return players;
    }

    public List<String> getTurnOrder(Long startPlayer){
        List<String> turnOrder = new ArrayList<>();
        turnOrder.add(Long.toString(startPlayer));

        int j = startPlayer.intValue();
        for(int i = 1; i<4; i++){
            if(j==4){
                j = 1;
            } else{
                j = j+1;
            }
            turnOrder.add(String.valueOf(j));
        }

        return turnOrder;
    }

    private static Player findPlayerInGame(Game game, String playerName){
        List<Player> playerList = game.getactive_Players();

        for(Player player: playerList){
            if(playerName.equals(player.getPlayerName())){
                return player;
            }
        }
        return null;
    }

    public void changePlayerStatus(Long gameId, String playerName, PlayerStatus status){
        Game game = findGame(gameId);
        Player player = findPlayerInGame(game, playerName);

        if(player == null){
            throw new IllegalStateException("Player not found");
        }
        player.setStatus(status);
    }

    public void setGameReady(Long gameId){
        Game game = findGame(gameId);
        List<Player> playerList = game.getactive_Players();
        int i = 0;
        for(Player player: playerList){
            if(player.getStatus()==PlayerStatus.READY){
                i=i+1;
            }
        }
        System.out.println(i);
        if(i==4){
            changeGameStatus(gameId, GameStatus.READY);
        }else if(i==3){
            changeGameStatus(gameId, GameStatus.ALMOST_READY);
        }
    }

    public String getWincondition(Long gameId, String userId){
        Game game = findGame(gameId);
        Player player = findPlayerById(game, Long.valueOf(userId));

        return player.getWinCondition();
    }

    public String getUltimateAttack(Long gameId, String userId){
        Game game = findGame(gameId);
        Player player = findPlayerById(game, Long.valueOf(userId));

        return player.getUltimate();
    }
}