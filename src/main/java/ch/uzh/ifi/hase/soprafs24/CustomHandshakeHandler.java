package ch.uzh.ifi.hase.soprafs24;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    // Create a principal with a unique name
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String userId = request.getURI().getQuery().split("userId=")[1].split("&")[0];
        return new Principal() {//NOSONAR

            @Override
            public String getName() {
                return userId;
            }
        };
    }
}
