package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameBoardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * GameBoard Controller
 * This class is responsible for handling all REST request that are related to
 * the gameBoard.
 * The controller will receive the request and delegate the execution to the
 * GameBoardService and finally return the result.
 */

@RestController
public class GameBoardController {

    private final GameBoardService gameBoardService;

    GameBoardController(GameBoardService gameBoardService) {
        this.gameBoardService = gameBoardService;
    }

    @GetMapping("/gameboard")
    @ResponseStatus(HttpStatus.OK)
    public List<GameBoardGetDTO> getAllGameBoards() {
        // fetch all gameboards in the internal representation
        List<GameBoard> gameBoards = gameBoardService.getGameBoards();
        List<GameBoardGetDTO> gameBoardGetDTOs = new ArrayList<>();
        // convert each gameboard to the API representation
        for (GameBoard gameBoard : gameBoards) {
            gameBoardGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameBoardGetDTO(gameBoard));
        }
        return gameBoardGetDTOs;
    }

    @PostMapping("/gameboards")
    @ResponseStatus(HttpStatus.CREATED)
    public GameBoardGetDTO createGameBoard() {
        // create gameboard
        GameBoard createdGameBoard = gameBoardService.createGameBoard();
        // convert internal representation of gameboard back to API
        return DTOMapper.INSTANCE.convertEntityToGameBoardGetDTO(createdGameBoard);
    }

    @GetMapping("/gameboard/spaces")
    @ResponseStatus(HttpStatus.OK)
    public List<GameBoardSpace> getAllGameBoardSpaces() {
        // get all spaces of initial gameboard
        return gameBoardService.loadAndSaveGameBoardSpaces();
    }

    @GetMapping("/gameboard/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GameBoardGetDTO gameboard(@PathVariable Long id) {
        GameBoard gameBoard = gameBoardService.getGameBoard(id);
        return DTOMapper.INSTANCE.convertEntityToGameBoardGetDTO(gameBoard);
    }
}
