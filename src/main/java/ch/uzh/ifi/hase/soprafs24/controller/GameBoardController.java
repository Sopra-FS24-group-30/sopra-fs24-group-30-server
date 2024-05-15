package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoard;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameBoardGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Effects.Getem;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private GameWebSocketController gameWebSocketController;

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

    public void pPlayer(Player player, Long teammateId, long pId, int cash, long posi, String wincondi){
        player.setPlayerId(pId);
        player.setTeammateId(teammateId);
        player.setCash(cash);
        player.setPosition(posi);
        player.setWinCondition(wincondi);
    }
    @GetMapping("/move")
    public Map<String, Object> doit(){
        Long lobbyId = gameManagementService.createGame("1");
        Game game = gameManagementService.findGame(lobbyId);
        GameFlow gameFlow = new GameFlow();
        gameFlow.setGameBoard(lobbyId);
        gameFlow.setGameId(lobbyId);
        gameFlow.setCurrentTurn(1);
        gameWebSocketController.addGame(lobbyId, game);
        gameWebSocketController.addGameFlow(lobbyId, gameFlow);
        Player p1 = new Player();
        pPlayer(p1, 3L, 1L, 59, 11L, "Company");
        p1.addItemNames("TheBrotherAndCo");
        Player p2 = new Player();
        pPlayer(p2, 4L, 2L, 150, 22L, "Golden");
        p2.setLandYellow(7);
        Player p3 = new Player();
        pPlayer(p3, 1L, 3L, 150, 33L, "Marooned");
        Player p4 = new Player();
        pPlayer(p4, 2L, 4L, 150, 44L, "Ship");
        p4.setLandCat(2);
        p4.addCardNames("B14");
        p4.addCardNames("B26");
        p4.setCanWinner(true);
        gameFlow.addPlayer(p1);
        gameFlow.addPlayer(p2);
        gameFlow.addPlayer(p3);
        gameFlow.addPlayer(p4);
        gameFlow.setTurnPlayerId(1L);
        gameFlow.getGameBoard().getSpaces().get(0).setIsGoal(true);
        return gameFlow.move(6, 18L);
    }


    @GetMapping("/cardPosition")
    public Map<String, Object> getAllCards() {
        Long lobbyId = gameManagementService.createGame("1");
        Game game = gameManagementService.findGame(lobbyId);
        GameFlow gameFlow = new GameFlow();
        gameFlow.setGameBoard(lobbyId);
        gameFlow.setGameId(lobbyId);
        gameFlow.setCurrentTurn(1);
        gameWebSocketController.addGame(lobbyId, game);
        gameWebSocketController.addGameFlow(lobbyId, gameFlow);
        Player p1 = new Player();
        pPlayer(p1, 3L, 1L, 15, 53L,"JackSparrow");
        p1.addItemNames("TheBrotherAndCo");
        Player p2 = new Player();
        pPlayer(p2, 4L, 2L, 15, 53L,"Golden");
        p2.setLandYellow(7);
        Player p3 = new Player();
        pPlayer(p3, 1L, 3L, 15, 53L,"Marooned");
        Player p4 = new Player();
        pPlayer(p4, 2L, 4L, 15, 53L, "Drunk");
        gameFlow.addPlayer(p1);
        gameFlow.addPlayer(p2);
        gameFlow.addPlayer(p3);
        gameFlow.addPlayer(p4);
        gameFlow.setTurnPlayerId(2L);
        gameFlow.getGameBoard().getSpaces().get(0).setIsGoal(true);
        p1.addCardNames("S1");
        JSONObject updateCardPositions = Getem.getCards().get("S1");
        JSONArray movesArray = updateCardPositions.getJSONArray("moves");

        int moves = movesArray.getInt(0);
        //System.out.println(Getem.getCards().get("B14"));
        return gameFlow.updateCardPositions(Getem.getCards().get("G13"), 3);

    }

    @GetMapping("/cards")
    public static HashMap<String, JSONObject> getCards() {
        return Getem.getCards();
    }
}
