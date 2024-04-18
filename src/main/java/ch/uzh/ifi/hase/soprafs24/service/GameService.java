package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameBoardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameBoardRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class GameService {

    private final GameRepository GameRepository;
    private final GameBoardRepository GameBoardRepository;
    private final GameBoardService gameBoardService;

    public GameService(@Qualifier("gameRepository") GameRepository GameRepository, @Qualifier("gameBoardRepository") GameBoardRepository GameBoardRepository, GameBoardService gameBoardService) {
        this.GameRepository = GameRepository;
        this.GameBoardRepository = GameBoardRepository;
        this.gameBoardService = gameBoardService;
    }

    public List<Game> getGames() {
        return this.GameRepository.findAll();
    }

    /**
     * create a new lobbyId and return it
     * @return a unique LobbyId
     */
    public Long getLobbyId(){
        Random rnd = new Random();
        long id = 100000 + rnd.nextInt(900000);
        while (this.GameRepository.findById(id) != null){
            id = 100000 + rnd.nextInt(900000);
        }
        return id;
    }

    public long createGame(String playerID){
        try{
            Game game = new Game();
            long id = getLobbyId();
            game.setId(id);

            List<String> playerList = new ArrayList<>();
            playerList.add(playerID);
            game.setPlayerIds(playerList);

            return id;
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"the server could not start the game correctly");
        }
    }

    public Game setUpGame(GamePostDTO GamePostDTO) {
        Game game = new Game();
        GameBoard gameBoard = gameBoardService.createGameBoard();
        game.setGameBoard(gameBoard);
        game.setId(GamePostDTO.getId());
        gameBoard.setStatus(GameBoardStatus.ACTIVE);
        game.setStatus(GameStatus.PLAYING);
        gameBoard.setGame(game);
        return GameRepository.saveAndFlush(game);
    }

    public Game getGame(Long id) {
        Optional<Game> Game = GameRepository.findById(id);
        return Game.orElse(null);
    }

}