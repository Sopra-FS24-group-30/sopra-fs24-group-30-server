package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameBoardService;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to
 * the Game.
 * The controller will receive the request and delegate the execution to the
 * GameService and finally return the result.
 */

@RestController
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/games") // <-- corrected endpoint path
    @ResponseStatus(HttpStatus.OK)
    public List<GameGetDTO> getAllGames() {
        // fetch all games in the internal representation
        List<Game> games = gameService.getGames();
        List<GameGetDTO> gameGetDTOs = new ArrayList<>();
        // convert each game to the API representation
        for (Game game : games) {
            gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(game));
        }
        return gameGetDTOs;
    }

    @PostMapping("/games") // <-- corrected endpoint path
    @ResponseStatus(HttpStatus.CREATED)
    public GameGetDTO createGame(@RequestBody GamePostDTO gamePostDTO) {
        // create game

        Game createdGame = gameService.createGame(gamePostDTO);
        System.out.println("Game created: " + createdGame.getId());
        // convert internal representation of game back to API
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(createdGame);
    }

    @GetMapping("/games/{id}") // <-- corrected endpoint path
    @ResponseStatus(HttpStatus.OK)
    public GameGetDTO getGame(@PathVariable Long id) {
        Game game = gameService.getGame(id);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }
}
