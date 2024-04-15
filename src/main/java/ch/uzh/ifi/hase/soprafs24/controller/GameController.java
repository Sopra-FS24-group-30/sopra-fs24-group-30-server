package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController{
    @MessageMapping("/game/start")
    @SendTo("/topic/game")
    public String startGame(String message){
        return "Game started" + message;
    }
}