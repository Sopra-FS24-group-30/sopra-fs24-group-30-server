package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.logic.Returns.*;
import ch.uzh.ifi.hase.soprafs24.service.GameManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Controller
public class GameWebSocketController {

    @Autowired
    private GameManagementService gameManagementService;

    @MessageMapping("/game/create")
    @SendTo("/topic/gameCreated")
    public Map<String, Object> createGame(String playerString) {
        Map<String, String> playerDict = gameManagementService.manualParse(playerString);
        Long gameId = gameManagementService.createGame(playerDict.get("playerId"));//NOSONAR
        Map <String, Object> response = new HashMap<>();
        response.put("message", "game created");
        response.put("gameId", String.valueOf(gameId));//NOSONAR
        return response;
    }


    //TODO: add handling here
    @MessageMapping("/game/item")
    @SendTo("/topic/game/cash")
    public static void handleEffect(String msg){

    }

    @SendTo("/topic/game/cash")
    public static CashData returnMoney(CashData cashData){
        return cashData;
    }

    @SendTo("/topic/game/move")
    public static MoveData returnMoves(MoveData move){
        return move;
    }

    @SendTo("/topic/game/usable")
    public static UsableData returnUsables(UsableData usableData){
        return usableData;
    }

    @SendTo("/topic/board/money") //alles wo während em spiel gschickt wird goht an topic/board
    public static Map<String, Map<String, Integer>> changeMoney(Player player, int change){
        return changeMoneys(Map.of(player, change));
    }

    //#region

    @SendTo("/topic/board/money")
    public static Map<String, Map<String, Integer>> changeMoney(Player player, int change, Player player2, int change2){
        return changeMoneys(Map.of(player, change, player2, change2));
    }

    @SendTo("/topic/board/money")
    public static Map<String, Map<String, Integer>> changeMoney(Player player, int change, Player player2, int change2, Player player3, int change3) {
        return changeMoneys(Map.of(player, change, player2, change2, player3, change3));
    }

    @SendTo("/topic/board/money")
    public static Map<String, Map<String, Integer>> changeMoney(Player player, int change, Player player2, int change2, Player player3, int change3, Player player4, int change4) { //NOSONAR overloading
        return changeMoneys(Map.of(player, change, player2, change2, player3, change3, player4, change4));
    }

    private static Map<String, Map<String, Integer>> changeMoneys(Map<Player, Integer> hoi) {
        Map<String, Map<String, Integer>> response = new HashMap<>();
        for (Map.Entry<Player, Integer> entry : hoi.entrySet()) {
            Player player = entry.getKey();
            int change = entry.getValue();
            int newAmount = Math.max(player.getCash()+change, 0);
            player.setCash(newAmount);

            // Prepare a detailed response for each player
            Map<String, Integer> details = new HashMap<>();
            details.put("newAmountOfMoney", newAmount);
            details.put("changeAmountOfMoney", change);
            response.put(player.getPlayerId().toString(), details);
        }
        return response;
    }
    //#endregion

    @MessageMapping("/game/join")
    @SendTo("/topic/gameJoined")
    public  Map<String, Object> joinGame(String msg) {
        System.out.println(msg);
        Map<String, String> message = gameManagementService.manualParse(msg);

        Long gameId = Long.valueOf(message.get("gameId"));
        String playerId = message.get("playerId");
        boolean joined = gameManagementService.joinGame(gameId, playerId);

        Map <String, Object> response = new HashMap<>();
        response.put("gameId", gameId);
        if (joined){
            response.put("joined", joined);
        }else{
            response.put("joined", false);
        }
        return response;
    }

    @MessageMapping("/game/lobby")
    @SendTo("/topic/players")
    public List<String> lobby(String msg){
        Map<String, String> message = gameManagementService.manualParse(msg);
        Long gameId = Long.valueOf(message.get("gameId"));

        List<String> response = gameManagementService.lobbyPlayers(gameId);
        return response;
    }

    @MessageMapping("/gameReady")
    @SendTo("/topic/gameReady")
    public Map<String, Object> gameReady(String msg){
        Map<String, String> message = gameManagementService.manualParse(msg);
        Long gameId = Long.valueOf(message.get("gameId"));
        Map<String, Object> response = new HashMap<>();
        if (gameManagementService.getPlayersInGame(gameId).size() == 4){
            response.put("gameReady", true);
        } else{
            response.put("gameReady", false);
        }
        return response;
    }

    @MessageMapping("/game/leave")
    public void leaveGame(String msg){
        Map<String, String> message = gameManagementService.manualParse(msg);
        Long gameId = Long.valueOf(message.get("gameId"));
        String playerId = message.get("playerId");

        gameManagementService.leaveGame(gameId, playerId);
    }

    @MessageMapping("/game/setUp")
    public void setUpGame(String msg){
        Map<String, String> message = gameManagementService.manualParse(msg);
        Long gameId = Long.valueOf(message.get("gameId"));
        gameManagementService.changeGameStatus(gameId, GameStatus.SETUP);
    }

    @MessageMapping("/game/status")
    @SendTo("/topic/game/status")
    public Map<String, String> gameStatus(String msg){
        Map<String, String> message = gameManagementService.manualParse(msg);
        Long gameId = Long.valueOf(message.get("gameId"));
        GameStatus status = gameManagementService.getGameStatus(gameId);
        Map<String, String> response = new HashMap<>();
        response.put("status", status.name());
        return response;
    }
}
