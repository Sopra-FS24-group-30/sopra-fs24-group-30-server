package ch.uzh.ifi.hase.soprafs24;
import ch.uzh.ifi.hase.soprafs24.WebSocketService;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Component
public class WebSocketEventListener{
    @Autowired
    private WebSocketService webSocketService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        System.out.println("here: " + event);
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        
        String userId = accessor.getUser().getName();

        System.out.println("New connection: userId: " +userId + "| sessionId: "+sessionId);
        webSocketService.registerUserSession(userId, sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        webSocketService.removeUserSession(sessionId);
    }
}