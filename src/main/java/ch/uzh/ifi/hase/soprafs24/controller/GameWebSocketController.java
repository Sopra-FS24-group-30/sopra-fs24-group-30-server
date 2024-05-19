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


    /*

    public static void main(String[] args){
        GameFlow gameFlow = new GameFlow();
        for(int i=1; i<=4; i++){
            Player p = new Player();
            p.setPlayerId((long) i);
            p.setCash(100);
            p.setPosition(30L);
            ArrayList<String> itemNames = new ArrayList<>();
            itemNames.add("OnlyFansAbo");
            p.addItemNames(itemNames);
            gameFlow.addPlayer(p);
        }
        gameFlow.setTurnPlayerId(1L);
        gameFlow.setGameId(123456L);
        gameFlows.put(123456L,gameFlow);
        handleUltimate("{\"ultimateUsed\": \"Chameleon\",\"choices\": {}}",123456L);
        System.out.println("player 1");
        System.out.println("cash: " + gameFlow.getPlayer(1).getCash());
        System.out.println("items" + gameFlow.getPlayer(1).getItemNames());
        System.out.println("player 2");
        System.out.println("cash: " + gameFlow.getPlayer(2).getCash());
        System.out.println("items" + gameFlow.getPlayer(2).getItemNames());
    }

     */

    private static SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameWebSocketController(SimpMessagingTemplate messagingTemplate){
        GameWebSocketController.messagingTemplate = messagingTemplate;
    }

    @Autowired
    private GameManagementService gameManagementService;

    //saving the current Game at the beginning
    private static HashMap<Long,Game> allGames = new HashMap<>();
    private static HashMap<Long,GameFlow> gameFlows = new HashMap<>();

    public static Game getCurrGame(Long lobbyId) {
        return allGames.get(lobbyId);
    }

    public static void setCurrGame(HashMap<Long,Game> currentGame) {
        allGames = currentGame;
    }

    public static void addGame(Long lobbyId, Game game){
        allGames.put(lobbyId,game);
    }

    public static void removeGame(Long lobbyId){
        allGames.remove(lobbyId);
    }

    //TODO: Setup the game

    public static GameFlow getGameFlow(Long lobbyId){
        return gameFlows.get(lobbyId);
    }

    public static void setGameFlow(HashMap<Long,GameFlow> currentGameFlow) {
        gameFlows = currentGameFlow;
    }

    public static void addGameFlow(Long lobbyId, GameFlow gameFlow){
        gameFlows.put(lobbyId,gameFlow);
    }

    public static void removeGameFlow(Long lobbyId){
        gameFlows.remove(lobbyId);
    }


    @MessageMapping("/game/create")
    public void createGame(String playerString) {
        Map<String, String> playerDict = gameManagementService.manualParse(playerString);
        String userId = playerDict.get("playerId");//NOSONAR

        System.out.println(userId);

        Long gameId = GameManagementService.createGame(userId);//NOSONAR
        Map <String, Object> response = new HashMap<>();
        response.put("message", "game created");
        response.put("gameId", String.valueOf(gameId));//NOSONAR

        String destination = "/queue/gameCreated";
        messagingTemplate.convertAndSendToUser(userId, destination, response);
    }

    @MessageMapping("/board/item/{gameId}")
    public static void handleItems(String msg, @DestinationVariable("gameId") Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        //extract Info from message
        JSONObject jsonObject = new JSONObject(msg);
        String itemName = jsonObject.getString("itemUsed");
        String effectName;
        JSONObject effectParas;
        JSONObject choices = jsonObject.getJSONObject("choices");
        gameFlow.setChoices(choices);
        //get the effect with paras
        HashMap<String, JSONObject> items = Getem.getItems();
        JSONObject effectComplete = items.get(itemName);
        effectName = effectComplete.keys().next();
        effectParas = effectComplete.getJSONObject(effectName);
        //remove the item from the players hand
        gameFlow.getPlayer(gameFlow.getTurnPlayerId().intValue()).removeItemNames(itemName);

        handleEffects(effectName,effectParas, gameId);
    }

    @MessageMapping("/board/test/{gameId}")
    public static void tes(String msg, @DestinationVariable("gameId") Long gameId){
        ArrayList<Integer> dice = new ArrayList<>();
        dice.add(5);
        DiceData diceData = new DiceData(dice);
        messagingTemplate.convertAndSend("/topic/board/goal/" + gameId, diceData);
    }

    @MessageMapping("/board/ultimate/{gameId}")
    public static void handleUltimate(String msg, @DestinationVariable("gameId") Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        //extract Info from message
        JSONObject jsonObject = new JSONObject(msg);
        String usable = jsonObject.getString("ultimateUsed");
        JSONObject choices = jsonObject.getJSONObject("choices");
        gameFlow.setChoices(choices);
        String effectName;
        JSONObject effectParas;
        //get the effect with paras
        HashMap<String, JSONObject> items = Getem.getUltimates();
        JSONObject effectComplete = items.get(usable);
        effectName = effectComplete.keys().next();
        effectParas = effectComplete.getJSONObject(effectName);
        //set the ultimate to disabled
        gameFlow.getPlayer(gameFlow.getTurnPlayerId().intValue()).setUltActive(false);

        handleEffects(effectName,effectParas,gameId);
    }

    public static void handleEffects(String effect, JSONObject effectParas, Long gameId){
        GameFlow gameFlow = gameFlows.get(gameId);
        switch (effect){
            case "updateMoney":
                gameFlow.updateMoney(effectParas);
                break;
            case "exchange":
                gameFlow.exchange(effectParas,new HashMap<Integer,ArrayList<String>>());
                break;
            case "givePlayerDice":
                gameFlow.givePlayerDice(effectParas);
                break;
            case "updatePositions":
                gameFlow.updatePositions(effectParas);
                break;
            case "shuffle":
                gameFlow.shuffle(effectParas);
                break;
            case "updateTurns":
                gameFlow.updateTurns(effectParas);
                break;
            case "useRandomUsable":
                gameFlow.useRandomUsable(effectParas);
                break;
            default:
                throw new RuntimeException("the defined effect does not exist");
        }
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

        if(response.get("status").equals("READY")){
            System.out.println("Attempting to send the playerIds");
            sendPlayerId(gameId);
        }

        String destination = "/topic/game/status/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    public void sendPlayerId(Long gameId){
        String destination = "/queue/game/PlayerId";

        Game game = gameManagementService.findGame(gameId);
        System.out.println(game);
        List<Player> playerList = game.getactive_Players();
        System.out.println(playerList);

        for(Player player: playerList){
            Map<String, String> response = new HashMap<>();
            response.put("playerId", String.valueOf(player.getPlayerId()));
            String userId = String.valueOf(player.getUserId());
            System.out.println("Sending the playerId of" + userId);
            messagingTemplate.convertAndSendToUser(userId, destination, response);
        }
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
        //to be implemented
    }

    @MessageMapping("/game/{gameId}/ultimateAttack")
    public void getUltimateAttack(@DestinationVariable Long gameId, @Payload String userId){
        //to be implemented
    }

    @MessageMapping("/board/dice/{gameId}")
    public static void diceWalk(@DestinationVariable Long gameId){
        rollOneDice(gameId);
        move(gameId);
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
        List<Object> players = gameManagementService.getInformationPlayers(gameId);

        response.put("players", players);


        String destination = "/queue/game/" + gameId +"/board/start";

        GameFlow gameFlow = new GameFlow();
        gameFlow.setGameId(gameId);
        gameFlow.setGameBoard(gameId);
        gameFlow.setCurrentTurn(1);
        int startingPlayer = (int) (Math.random()*4+1);
        gameFlow.setTurnPlayerId((long) startingPlayer);
        List<Player> activePlayers = allGames.get(gameId).getactive_Players();
        for(Player player : activePlayers){
            gameFlow.addPlayer(player);
        }
        gameFlows.put(gameId,gameFlow);

        List<String> turnOrder = gameManagementService.getTurnOrder(gameFlow.getTurnPlayerId());

        response.put("TurnOrder", turnOrder);

        messagingTemplate.convertAndSend(destination, response);
        gameManagementService.changeGameStatus(gameId, GameStatus.PLAYING);
    }


    public void firstPlayerTurn(Long gameId){
        GameFlow gameFlow = getGameFlow(gameId);
        Map<String, Object> response = new HashMap<>();
        response.put("currentTurn", gameFlow.getCurrentTurn());
        response.put("activePlayer", gameFlow.getTurnPlayerId());

        String destination = "/topic/game/" + gameId + "/board/newActivePlayer";
        messagingTemplate.convertAndSend(destination, response);

    }

    @MessageMapping("/board/cards/{gameId}")
    public void handleCardPosition(@Payload Map<String, String> payload, @DestinationVariable("gameId") Long gameId){
        String selectedCard = payload.get("usableUsed");
        int count = -123;

        if (payload.get("choice")!= null){
            JSONObject choiceJson = new JSONObject(payload.get("choice"));
            Map<String, Object> choice = choiceJson.toMap();
            // Extract the count value from the choice map
            count = (int) choice.getOrDefault("count", -123);
        }
        JSONObject card = Getem.getCards().get(selectedCard);
        String destination = "/topic/board/cards/" + gameId;
        GameFlow gameFlow = gameFlows.get(gameId);
        messagingTemplate.convertAndSend(destination, gameFlow.updateCardPositions(card, count));
    }


    @MessageMapping("/board/junction/{gameId}")
    public void contJunction(@DestinationVariable Long gameId, @Payload Map<String, Long> payload){
        long selectedSpace = payload.get("selectedSpace");
        String destination = "/topic/board/junction/" + gameId;
        GameFlow gameFlow = gameFlows.get(gameId);
        messagingTemplate.convertAndSend(destination, gameFlow.move(gameFlow.getMovesLeft(), selectedSpace));
    }

    public static void rollOneDice(Long gameId) { //one die throw
        Map<String, Object> response = new HashMap<>();
        GameFlow gameFlow = gameFlows.get(gameId);
        List<Integer> dice = gameFlow.throwDice(1);
        gameFlow.setMovesLeft(dice.get(0));
        response.put("results", dice);
        String destination = "/topic/board/dice/" + gameId;
        messagingTemplate.convertAndSend(destination, response);
    }

    public static void move(Long gameId){
        String destination = "/topic/board/move/" + gameId;
        GameFlow gameFlow = gameFlows.get(gameId);
        messagingTemplate.convertAndSend(destination, gameFlow.move(gameFlow.getMovesLeft(), gameFlow.getPlayers()[(int)(long)(gameFlow.getTurnPlayerId())].getPosition()));
    }

    public static void returnJunction(Map<String, Object> chooseJunctionMsg, Long gameId, Long playerId){
        String destination = "/topic/board/junction/" + gameId + "/" + playerId;
        messagingTemplate.convertAndSend(destination, chooseJunctionMsg);
    }

    public static void changeGoal(List<GameBoardSpace> spaces, Long gameId){
        String destination = "/topic/board/goal/" + gameId;
        GameFlow gameFlow = gameFlows.get(gameId);
        messagingTemplate.convertAndSend(destination, gameFlow.setBoardGoal(spaces));
    }

    public static void newPlayer(Map<String, Object> nextTurnMsg, Long gameId){
        String destination = "/topic/board/newActivePlayer/" + gameId;
        messagingTemplate.convertAndSend(destination, nextTurnMsg);
    }

    public static void specItem(Map<String, Object> getItemMsg, Long gameId){
        String destination = "/topic/board/usable/" + gameId;
        messagingTemplate.convertAndSend(destination, getItemMsg);
    }

    public static void winCondiProgress(Map<String, Object> winCondiUpdate, Long playerId, Long gameId){
        String destination = "/topic/board/winCondition/" + gameId + "/" + playerId;
        messagingTemplate.convertAndSend(destination, winCondiUpdate);
    }

    public static void changeCash(Map<String, Object> cashmsg, Long gameId){
        String destination = "/topic/board/money/" + gameId;
        messagingTemplate.convertAndSend(destination, cashmsg);
    }

    public static void returnMoney(CashData cashData, Long gameId) {
        String destination = "/topic/board/money" + gameId; //NOSONAR
        messagingTemplate.convertAndSend(destination,cashData);
    }

    public static void returnMoves(MoveData moveData, Long gameId) {
        String destination = "/topic/board/move" + gameId;
        messagingTemplate.convertAndSend(destination, moveData);
    }

    public static void returnMoves(Map<String, Object> moveData, Long gameId) {
        String destination = "/topic/board/move" + gameId;
        messagingTemplate.convertAndSend(destination, moveData);
    }

    public static void returnUsables(UsableData usableData, Long gameId) {
        String destination = "/topic/board/usables" + gameId;
        messagingTemplate.convertAndSend(destination, usableData);
    }

    public static void returnDice(DiceData diceData, Long gameId){
        String destination = "/topic/board/dice" + gameId;
        messagingTemplate.convertAndSend(destination, diceData);
    }

    public void endGame(Map<String, String> endGameMsg, Long gameId){
        String destination = "/topic/board/gameEnd" + gameId;
        messagingTemplate.convertAndSend(destination, endGameMsg);
        gameManagementService.changeGameStatus(gameId, GameStatus.NOT_PLAYING);
    }

    @MessageMapping("/game/ranking/{gameId}")
    public void gameRank(@DestinationVariable Long gameId){
        Map<String, Object> winMsg = getGameFlow(gameId).getWinMsg();
        String destination = "/topic/ranking/" + gameId;
        messagingTemplate.convertAndSend(destination, winMsg);
    }

    public static void returnUltToPlayer(UltimateData ultimateData, Long gameId, Long userId){
        String destination = "/topic/board/ultimate" + gameId;
        messagingTemplate.convertAndSendToUser(userId.toString(),destination,ultimateData);
    }

    public static void returnTurnActive(TurnActiveData turnActiveData, Long gameId){
        String destination = "/topic/board/newActivePlayer" + gameId;
        messagingTemplate.convertAndSend(destination,turnActiveData);
    }
}