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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.ArrayList;

@Controller
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameWebSocketController(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    //saving GameId at the beginning
    private static Long gameId;
    public static Long getGameId() {
        return gameId;
    }
    public static void setGameId(Long gameId) {
        GameWebSocketController.gameId = gameId;
    }

    //saving the current Game at the beginning
    private static Game currGame;
    public static Game getCurrGame() {
        return currGame;
    }
    public static void setCurrGame(Game currentGame) {
        currGame = currentGame;
    }

    //saving the moves (dice throws or card usages), used to call the move function in GameFlow
    private static int movesLeft;
    public static void setMovesLeft(int movesLeft) {
        GameWebSocketController.movesLeft = movesLeft;
    }
    public static int getMovesLeft() {
        return movesLeft;
    }

    //TODO: Setup the game
    private static GameFlow gameFlow = new GameFlow();

    @Autowired
    private GameManagementService gameManagementService;

    @MessageMapping("/game/create")
    public void createGame(String playerString) {
        Map<String, String> playerDict = gameManagementService.manualParse(playerString);
        String userId = playerDict.get("playerId");

        System.out.println(userId);

        Long gameId = GameManagementService.createGame(userId);//NOSONAR
        Map <String, Object> response = new HashMap<>();
        response.put("message", "game created");
        response.put("gameId", String.valueOf(gameId));//NOSONAR

        String destination = "/queue/gameCreated";
        messagingTemplate.convertAndSendToUser(userId, destination, response);
    }

    //TODO: add handling here add support for choices
    @MessageMapping("/board/usable")
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

    @MessageMapping("/game/{gameId}/lobby")
    public void lobby(@DestinationVariable Long gameId){
        List<String> response = gameManagementService.lobbyPlayers(gameId);

        String destination = "/topic/players/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    @MessageMapping("/game/{gameId}/gameReady")
    public void gameReady(@DestinationVariable Long gameId){
        Map<String, Object> response = new HashMap<>();
        if (gameManagementService.getPlayersInGame(gameId).size() == 4){
            response.put("gameReady", true);
        } else{
            response.put("gameReady", false);
        }

        String destination = "/topic/gameReady/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    @MessageMapping("/game/leave")
    public void leaveGame(String msg){
        Map<String, String> message = gameManagementService.manualParse(msg);
        Long gameId = Long.valueOf(message.get("gameId"));
        String playerId = message.get("playerId");

        gameManagementService.leaveGame(gameId, playerId);
    }

    @MessageMapping("/game/{gameId}/setUp")
    public void setUpGame(@DestinationVariable Long gameId){
        gameManagementService.changeGameStatus(gameId, GameStatus.SETUP);
    }

    @MessageMapping("/game/{gameId}/status")
    public void gameStatus(@DestinationVariable Long gameId){
        System.out.println("Get Status");

        GameStatus status = gameManagementService.getGameStatus(gameId);
        Map<String, String> response = new HashMap<>();
        response.put("status", status.name());
        System.out.println(response);

        String destination = "/topic/game/status/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    @MessageMapping("/game/{gameId}/players")
    public void getPlayers(@DestinationVariable Long gameId, @Payload Map<String, Object> payload){
        System.out.println("getPlayers");

        String hostName = (String) payload.get("host");
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

        String destination = "/topic/game/players/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    @MessageMapping("/game/{gameId}/setTeammate")
    public void setTeammates(@DestinationVariable Long gameId, @Payload Map<String, Object> payload){

        Game game = gameManagementService.findGame(gameId);
        String player1 = (String) payload.get("host");
        String player2 = (String) payload.get("teammate");

        gameManagementService.setTeams(game, player1, player2);
    }

    @MessageMapping("/board/dice")
    public void diceWalk(){
        rollOneDice();
        move();
        //space effect maybe
        //call next player somehow
    }

    @MessageMapping("/board/junction")
    public Map<String, Object> contJunction(String msg){
        Map<String, String> message = gameManagementService.manualParse(msg);
        long selectedSpace = Long.parseLong(message.get("selectedSpace"));
        return GameFlow.move(movesLeft, selectedSpace);
    }

    @SendTo("/topic/board/dice")
    public Map<String, Object> rollOneDice() { //one die throw
        Map<String, Object> response = new HashMap<>();
        List<Integer> dice = GameFlow.throwDice();
        setMovesLeft(dice.get(0));
        response.put("results", dice);
        return response;
    }

    @SendTo("/topic/board/move")
    public Map<String, Object> move(){
        return GameFlow.move(movesLeft, GameFlow.getPlayers()[(int)(long)(GameFlow.getTurnPlayerId())].getPosition());
    }

    @SendTo("/topic/board/move")
    public static Map<String, Object> juncMove(Map<String, Object> partialMoveMsg){
        return partialMoveMsg;
    }

    @SendTo("/topic/board/junction")
    public static Map<String, Object> juncJunc(Map<String, Object> chooseJunctionMsg){
        return chooseJunctionMsg;
    }

    @SendTo("/topic/board/goal")
    public static Map<String, Long> changeGoal(List<GameBoardSpace> spaces){
        return GameFlow.setBoardGoal(spaces);
    }

    @SendTo("/topic/board/newActivePlayer")
    public static Map<String, Object> newPlayer(Map<String, Object> nextTurnMsg){
        return nextTurnMsg;
    }

    @SendTo("/topic/board/gameEnd")
    public static Map<String, Object> endy(Map<String, Object> endGameMsg){
        return endGameMsg;
    }

    @SendTo("/topic/board/usable")
    public static Map<String, Object> specItem(Map<String, Object> getItemMsg){
        return getItemMsg;
    }
}
