package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Effects.Getem;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.*;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.service.GameManagementService;
import ch.uzh.ifi.hase.soprafs24.entity.Game;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.ArrayList;

@Controller
public class GameWebSocketController {

    //TODO SIMP NOT STATIC BUT FINAL
    private static SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameWebSocketController(SimpMessagingTemplate messagingTemplate){
        GameWebSocketController.messagingTemplate = messagingTemplate;
    }

    private static Long gameId;
    public static Long getGameId() {
        return gameId;
    }
    public static void setGameId(Long gameId) {
        GameWebSocketController.gameId = gameId;
    }

    private static Game currGame;

    //saving the current Game at the beginning
    private static HashMap<Long,Game> allGames = new HashMap<>();

    //TODO WHICH GET CURR GAME
    public static Game getCurrGame(Long lobbyId) {
        return allGames.get(lobbyId);
    }
    public static Game getCurrGame() {
        return currGame;

     }
    public static void setCurrGame(HashMap<Long,Game> currentGame) {
        allGames = currentGame;
     }

     public static void addGame(Long lobbyId, Game game){
        allGames.put(lobbyId,game);
     }

    //TODO: Setup the game
    private static GameFlow gameFlow = new GameFlow();

    @Autowired
    private GameManagementService gameManagementService;

    @MessageMapping("/game/create")
    @SendTo("/topic/gameCreated")
    public Map<String, Object> createGame(String playerString) {
        Map<String, String> playerDict = gameManagementService.manualParse(playerString);
        Long gameId = GameManagementService.createGame(playerDict.get("playerId"));//NOSONAR
        Map <String, Object> response = new HashMap<>();
        response.put("message", "game created");
        response.put("gameId", String.valueOf(gameId));//NOSONAR
        return response;
    }

    @MessageMapping("/board/cards")
    @SendTo("/topic/board/cards")
    public static void handleCards(String msg){
        //idk what to do here
    }


    //TODO: add handling here add support for choices
    @MessageMapping("/board/usable/{gameId}")
    @SendTo("/topic/board/cash")
    public static void handleEffect(String msg){
        JSONObject jsonObject = new JSONObject(msg);
        String key = jsonObject.keys().next();
        String usable = jsonObject.getString(key);
        String effect;
        JSONObject effectParas;

        if (key.equals("itemUsed")) {
            HashMap<String, JSONObject> items = Getem.getItems();
            JSONObject effectComplete = items.get(usable);
            effect = effectComplete.keys().next();
            effectParas = effectComplete.getJSONObject(effect);
        }
        else{
            HashMap<String, JSONObject> items = Getem.getUltimates();
            JSONObject effectComplete = items.get(usable);
            effect = effectComplete.keys().next();
            effectParas = effectComplete.getJSONObject(effect);
        }
        switch (effect){
            case "updateMoney":
                gameFlow.updateMoney(effectParas);
                break;
            case "exchange":
                //TODO: insert choices here
                gameFlow.exchange(effectParas,new HashMap<Integer,ArrayList<String>>());
                break;
            case "givePlayerDice":
                gameFlow.givePlayerDice(effectParas);
                break;
            case "updatePositions":
                gameFlow.updatePositions(effectParas);
                break;
            default:
                throw new RuntimeException("the defined effect does not exist");
        }

    }

    @SendTo("/topic/board/cash")
    public static CashData returnMoney(CashData cashData) {
        return cashData;
    }

    @SendTo("/topic/board/move")
    public static MoveData returnMoves(MoveData move) {
        return move;
    }

    @SendTo("/topic/board/usable")
    public static UsableData returnUsables(UsableData usableData) {
        return usableData;
    }

    @MessageMapping("/game/join")
    @SendTo("/topic/gameJoined")
    public  Map<String, Object> joinGame(String msg) {
        System.out.println(msg);
        Map<String, String> message = gameManagementService.manualParse(msg);

        Long gameId = Long.valueOf(message.get("gameId"));
        String userId = message.get("playerId");
        boolean joined = gameManagementService.joinGame(gameId, userId);

        Map <String, Object> response = new HashMap<>();
        response.put("gameId", gameId);
        if (joined){
            response.put("joined", joined);
        }else{
            response.put("joined", false);
        }
        System.out.println("Joining");
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
        System.out.println("Get Status");
        Map<String, String> message = gameManagementService.manualParse(msg);
        Long gameId = Long.valueOf(message.get("gameId"));
        GameStatus status = gameManagementService.getGameStatus(gameId);
        Map<String, String> response = new HashMap<>();
        response.put("status", status.name());
        System.out.println(response);
        return response;
    }

    @MessageMapping("/game/players")
    @SendTo("/topic/game/players")
    public Map<String, Object> getPlayers(String msg){
        System.out.println("getPlayers");
        Map<String, String> message = gameManagementService.manualParse(msg);
        Long gameId = Long.valueOf(message.get("gameId"));

        String hostName = message.get("host");
        System.out.println(hostName);

        List<Player> players = gameManagementService.getActivePlayers(gameId);
        System.out.println(players);
        List<String> playerNames = new ArrayList<>();

        for(Player p: players){
            String name = p.getPlayerName();
            playerNames.add(name);
        }

        playerNames.remove(hostName);
        Map<String, Object> response = new HashMap<>();
        response.put("players", playerNames);
        return response;
    }


    @MessageMapping("/game/setTeammate")
    public void setTeammates(String msg){
        Map<String, String> message = gameManagementService.manualParse(msg);
        Long gameId = Long.valueOf(message.get("gameId"));
        Game game = gameManagementService.findGame(gameId);
        String player1 = message.get("host");
        String player2 = message.get("teammate");

        gameManagementService.setTeams(game, player1, player2);
    }

    @MessageMapping("/board/dice/{gameId}")
    public static void diceWalk(){
        rollOneDice();
        move();
        //space effect maybe
        //call next player somehow
    }

    @MessageMapping("/board/junction/{gameId}")
    public void contJunction(@DestinationVariable Long gameId, @Payload Map<String, Long> payload){
        long selectedSpace = payload.get("selectedSpace");
        String destination = "/topic/board/junction/" + gameId;
        messagingTemplate.convertAndSend(destination, gameFlow.move(gameFlow.getMovesLeft(), selectedSpace));
    }

    public static void rollOneDice() { //one die throw
        Map<String, Object> response = new HashMap<>();
        List<Integer> dice = GameFlow.throwDice();
        System.out.println("diceee: "+dice);
        GameFlow.setMovesLeft(dice.get(0));
        response.put("results", dice);
        String destination = "/topic/board/dice/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    public static void move(){
        String destination = "/topic/board/move/" + gameId;
        messagingTemplate.convertAndSend(destination, GameFlow.move(GameFlow.getMovesLeft(), GameFlow.getPlayers()[(int)(long)(GameFlow.getTurnPlayerId())].getPosition()));
    }

    public static void juncMove(Map<String, Object> partialMoveMsg){
        String destination = "/topic/board/move/" + gameId;
        messagingTemplate.convertAndSend(destination, partialMoveMsg);
    }

    public static void juncJunc(Map<String, Object> chooseJunctionMsg, Long playerId){
        String destination = "/topic/board/junction/" + gameId + "/" + playerId;
        messagingTemplate.convertAndSend(destination, chooseJunctionMsg);
    }

    public static void changeGoal(List<GameBoardSpace> spaces){
        String destination = "/topic/board/goal/" + gameId;
        messagingTemplate.convertAndSend(destination, GameFlow.setBoardGoal(spaces));
    }

    public static void newPlayer(Map<String, Object> nextTurnMsg){
        String destination = "/topic/board/newActivePlayer/" + gameId;
        messagingTemplate.convertAndSend(destination, nextTurnMsg);
    }

    public static void endy(Map<String, Object> endGameMsg){
        String destination = "/topic/board/gameEnd/" + gameId;
        messagingTemplate.convertAndSend(destination, endGameMsg);
    }

    public static void specItem(Map<String, Object> getItemMsg){
        String destination = "/topic/board/usable/" + gameId;
        messagingTemplate.convertAndSend(destination, getItemMsg);
    }

    public static void winCondiProgress(Map<String, Object> winCondiUpdate, Long playerId){
        String destination = "/topic/board/winCondition/" + gameId + "/" + playerId;
        messagingTemplate.convertAndSend(destination, winCondiUpdate);
    }

    public static void changeCash(Map<String, Object> cashmsg){
        String destination = "/topic/board/money/" + gameId;
        messagingTemplate.convertAndSend(destination, cashmsg);
    }
}