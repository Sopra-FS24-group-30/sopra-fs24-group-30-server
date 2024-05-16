package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Effects.Getem;
import ch.uzh.ifi.hase.soprafs24.logic.Returns.*;
import ch.uzh.ifi.hase.soprafs24.entity.GameBoardSpace;
import ch.uzh.ifi.hase.soprafs24.logic.Game.GameFlow;
import ch.uzh.ifi.hase.soprafs24.service.GameManagementService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.logic.Game.Player;
import ch.uzh.ifi.hase.soprafs24.constant.PlayerStatus;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.ArrayList;

@Controller
public class GameWebSocketController {

    private static SimpMessagingTemplate messagingTemplate;

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

    private static Game currGame;

    //saving the current Game at the beginning
    private static HashMap<Long,Game> allGames = new HashMap<>();

    //TODO WHICH GET CURR GAME
    public static Game getGameByLobbyId(Long lobbyId) {
        return allGames.get(lobbyId);
    }

    public static void setCurrGame(Game currentGame) {
        currGame = currentGame;
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

    @MessageMapping("/game/{gameId}/wincondition")
    public void getWincondition(@DestinationVariable Long gameId, @Payload String userId){
    }

    @MessageMapping("/game/{gameId}/ultimateAttack")
    public void getUltimateAttack(@DestinationVariable Long gameId, @Payload String userId){
    }

    @MessageMapping("/game/{gameId}/playerAtLP")
    public void playersAtLoadingPage(@DestinationVariable Long gameId, @Payload Map<String, String> player){
        String playerName = player.get("username");
        gameManagementService.changePlayerStatus(gameId, playerName, PlayerStatus.READY);
        gameManagementService.setGameReady(gameId);
    }

    @MessageMapping("/game/{gameId}/board/start")
    public void startGame(@DestinationVariable Long gameId, @Payload String userId){
        HashMap<String, Object> response = new HashMap<>();
        Game game = gameManagementService.findGame(gameId);
        Map<String, Object> players = gameManagementService.getInformationPlayers(gameId, Long.valueOf(userId));

        for(Map.Entry<String, Object> player: players.entrySet()){
            response.put(player.getKey(), player.getValue());
        }

        String destination = "/queue/game/" + gameId +"/board/start";

        messagingTemplate.convertAndSendToUser(userId, destination, response);
        gameManagementService.changeGameStatus(gameId, GameStatus.PLAYING);
    }

    @MessageMapping("/board/dice")
    public void diceWalk(){
        // TODO: Change the arguments being passed on
        rollOneDice(1L);
        move(1L);
        //space effect maybe
        //call next player somehow
    }
    @MessageMapping("/board/cards")
    //@SendTo("/topic/board/cards")
    public void handleCardPosition(@Payload Map<String, String> payload){
        String selectedCard = payload.get("usableUsed");
        int count = -123;

        if (payload.get("choice")!= null){
            JSONObject choiceJson = new JSONObject(payload.get("choice"));
            Map<String, Object> choice = choiceJson.toMap();
            // Extract the count value from the choice map
            count = (int) choice.getOrDefault("count", -123);
        }
        JSONObject card = Getem.getCards().get(selectedCard);
        String destination = "/topic/board/cards";
        messagingTemplate.convertAndSend(destination, gameFlow.updateCardPositions(card, count));
    }


    @MessageMapping("/board/junction/{gameId}")
    public void contJunction(@DestinationVariable Long gameId, @Payload Map<String, Long> payload){
        long selectedSpace = payload.get("selectedSpace");
        String destination = "/topic/board/junction/" + gameId;
        messagingTemplate.convertAndSend(destination, gameFlow.move(gameFlow.getMovesLeft(), selectedSpace));
    }

    public static void rollOneDice(Long gameIde) { //one die throw
        Map<String, Object> response = new HashMap<>();
        List<Integer> dice = GameFlow.throwDice();
        GameFlow.setMovesLeft(dice.get(0));
        response.put("results", dice);
        String destination = "/topic/board/dice/" + gameIde;
        messagingTemplate.convertAndSend(destination, response);
    }

    public static void move(Long gameId){
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
}