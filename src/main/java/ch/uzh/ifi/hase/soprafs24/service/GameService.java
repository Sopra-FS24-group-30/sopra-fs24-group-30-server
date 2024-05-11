package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.logic.Game.WinCondition;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Ultimate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
        Random rnd = new Random();
        long id = 100000 + rnd.nextInt(900000);
        while (this.gameRepository.findById(id) != null){
            id = 100000 + rnd.nextInt(900000);
        }
        return id;
    }

    public Player createPlayerForGame(User user, int currentPlayerCount, Game game) {
        // This method could be part of setting up a new game or joining an existing one
        if (currentPlayerCount >= 4) {
            throw new IllegalStateException("Cannot add more players to the game. The game is full.");
        }

        Player player = new Player();
        player.setPlayerId((long) (currentPlayerCount + 1)); // Associate the User with the Player
        player.setUser(user); // Associate the User with the Player
        // Initialize other properties of Player
        player.setPlayerName(user.getUsername());
        player.setCash(15);
        player.setWinCondition(WinCondition.getRandomWinCondition(player.getPlayerId(), game));
        player.setUltimate(Ultimate.getRandomUltis(player.getPlayerId(), game));
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