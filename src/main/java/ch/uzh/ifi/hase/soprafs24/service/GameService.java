package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.logic.Game.AchievementProgress;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs24.logic.Game.WinConditionUltimate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class GameService {

    private final Random random = new Random();
    private final GameRepository gameRepository;
    private final GameBoardService gameBoardService;

    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, GameBoardService gameBoardService) {
        this.gameRepository = gameRepository;
        this.gameBoardService = gameBoardService;
    }

    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }

    /**
     * create a new lobbyId and return it
     * @return a unique LobbyId
     */
    public Long getLobbyId(){
        long id = 100000 + this.random.nextInt(900000);
        int counter = 0;
        while (this.gameRepository.findById(id) != null){
            id = 100000 + this.random.nextInt(900000);
            if (counter > 10000){
                break;
            }
            counter++;
        }
        return id;
    }

    public Player createPlayerForGame(User user, int currentPlayerCount, Game game) {
        // This method could be part of setting up a new game or joining an existing one
        if (currentPlayerCount >= 4) {
            throw new IllegalStateException("Cannot add more players to the game. The game is full.");
        }

        Player player = new Player();
        player.setAchievementProgress(new AchievementProgress(user.getId()));
        player.setPlayerId((long) (currentPlayerCount + 1)); // Associate the User with the Player
        player.setUser(user); // Associate the User with the Player
        player.setUserId(user.getId());
        // Initialize other properties of Player
        player.setStatus(PlayerStatus.NOT_PLAYING);
        player.setPlayerName(user.getUsername());
        player.setCash(15);
        player.setWinCondition(WinConditionUltimate.getRandomWinCondition(player.getPlayerId(), game));
        player.setUltimate(WinConditionUltimate.getRandomUltimate(player.getPlayerId(), game));
        return player;
    }
    public Game setUpGame() {
        Game game = new Game();
        GameBoard gameBoard = gameBoardService.createGameBoard();
        game.setGameBoard(gameBoard);
        List<Player> players = new ArrayList<>();
        game.setactive_players(players);
        return game;
    }

    public long createGame(String playerID){
        try{
            Game game = setUpGame();
            long id = getLobbyId();
            game.setId(id);

            List<String> playerList = new ArrayList<>();
            playerList.add(playerID);
            game.setPlayers(playerList);

            return id;
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"the server could not start the game correctly");
        }
    }

    public Game getGame(Long id) {
        Optional<Game> game = gameRepository.findById(id);
        return game.orElse(null);
    }
}