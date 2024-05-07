package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.logic.Game.WinCondition;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameBoardService;
import ch.uzh.ifi.hase.soprafs24.service.GameManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    @Autowired
    private GameManagementService gameManagementService;

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

    /**
     * test move function with rest
     * should be deleted at the end
     * or like implemented as actual test
     */
    public void pPlayer(Player player, long pId, int cash, long posi, WinCondition wincondi){
        player.setPlayerId(pId);
        player.setCash(cash);
        player.setPosition(posi);
        player.setWinCondition(wincondi);
    }
    @GetMapping("/move/{lobbyId}")
    public Map<String, Object> doit(@DestinationVariable("lobbyId") Long lobbyId){
        GameManagementService.createGame("1");
        GameFlow.setGameBoard(lobbyId);
        Player p1 = new Player();
        pPlayer(p1, 1L, 15, 53L, new WinCondition("goldenIsMy..."));
        p1.addItemNames("TheBrotherAndCo");
        Player p2 = new Player();
        pPlayer(p2, 2L, 15, 53L, new WinCondition("goldenIsMy..."));
        p2.setLandYellow(7);
        Player p3 = new Player();
        pPlayer(p3, 3L, 15, 53L, new WinCondition("goldenIsMy..."));
        Player p4 = new Player();
        pPlayer(p4, 4L, 15, 53L, new WinCondition("goldenIsMy..."));
        GameFlow.addPlayer(p1);
        GameFlow.addPlayer(p2);
        GameFlow.addPlayer(p3);
        GameFlow.addPlayer(p4);
        GameFlow.setTurnPlayerId(2L);
        GameFlow.setCurrentTurn();
        GameFlow.getGameBoard().getSpaces().get(0).setIsGoal(true);
        return GameFlow.move(3, 24L);
    }
}
