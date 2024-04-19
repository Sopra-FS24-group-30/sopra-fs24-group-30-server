package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.GameManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.HashMap;

@Controller
public class GameWebSocketController {

    @Autowired
    private GameManagementService gameManagementService;

    @MessageMapping("/game/create")
    @SendTo("/topic/gameCreated")
    public Map<String, Object> createGame(String playerId) {
        Long gameId = gameManagementService.createGame(playerId);
        Map <String, Object> response = new HashMap<>();
        response.put("message", "game created");
        response.put("gameId", String.valueOf(gameId));
        return response;
    }

    @MessageMapping("/game/join")
    @SendTo("/topic/gameJoined")
    public String joinGame(Long gameId, String sessionId) {
        boolean joined = gameManagementService.joinGame(gameId, sessionId);
        if (joined) {
            return "Player joined game: " + gameId;
        } else {
            return "Failed to join game: " + gameId;
        }
    }
}
