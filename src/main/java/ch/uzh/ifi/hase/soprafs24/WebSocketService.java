package ch.uzh.ifi.hase.soprafs24;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {

    // Maps to keep track of user sessions
    private ConcurrentHashMap<String, String> userSessionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> sessionIdUserMap = new ConcurrentHashMap<>();

    /**
     * Registers a user session by associating the userId with their WebSocket sessionId.
     *
     * @param userId The unique identifier of the user.
     * @param sessionId The WebSocket session ID associated with the user.
     */
    public void registerUserSession(String userId, String sessionId) {
        userSessionMap.put(userId, sessionId);
        sessionIdUserMap.put(sessionId, userId);
        System.out.println("Registered session: user ID " + userId + " with session ID " + sessionId);
    }

    /**
     * Removes a user session based on the WebSocket sessionId.
     *
     * @param sessionId The WebSocket session ID to be removed.
     */
    public void removeUserSession(String sessionId) {
        String userId = sessionIdUserMap.get(sessionId);
        if (userId != null) {
            userSessionMap.remove(userId);
            sessionIdUserMap.remove(sessionId);
            System.out.println("Removed session: user ID " + userId + " with session ID " + sessionId);
        } else {
            System.out.println("No session found with ID " + sessionId + " to remove.");
        }
    }

    /**
     * Retrieves the WebSocket sessionId associated with a given userId.
     *
     * @param userId The unique identifier of the user.
     * @return The WebSocket sessionId associated with the user.
     */
    public String getSessionIdByUserId(String userId) {
        return userSessionMap.get(userId);
    }

    /**
     * Retrieves the userId associated with a given WebSocket sessionId.
     *
     * @param sessionId The WebSocket session ID.
     * @return The userId associated with the given session ID.
     */
    public String getUserIdBySessionId(String sessionId) {
        return sessionIdUserMap.get(sessionId);
    }
}
