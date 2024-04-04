package ch.uzh.ifi.hase.soprafs24.controller;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.entity.UserInformation;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardPostDTO;
import ch.uzh.ifi.hase.soprafs24.repository.GameBoardSpaceRepository;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameBoardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class GameBoardController {

    private final GameBoardService gameBoardService;

    GameBoardController(GameBoardService gameBoardService) {
        this.gameBoardService = gameBoardService;
    }

    @GetMapping("/gameboard")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameBoardGetDTO> getAllGameBoards() {
        // fetch all users in the internal representation
        List<GameBoard> gameBoards = gameBoardService.getGameBoards();
        List<GameBoardGetDTO> gameBoardGetDTOs = new ArrayList<>();
        // convert each user to the API representation
        for (GameBoard gameBoard : gameBoards) {
            gameBoardGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameBoardGetDTO(gameBoard));
        }
        return gameBoardGetDTOs;
    }
    @GetMapping("/gameboard/spaces")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameBoardSpace> getAllGameBoardSpaces() {
        // This method should handle the logic to load spaces and possibly save them
        return gameBoardService.loadAndSaveGameBoardSpaces();
    }

}
