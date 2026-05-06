package com.Spring_chat.Web_chat.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Component
public class WebSocketPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        Long userId = (Long) attributes.get(WebSocketAuthHandshakeInterceptor.ATTR_USER_ID);
        String username = (String) attributes.get(WebSocketAuthHandshakeInterceptor.ATTR_USERNAME);

        if (userId != null && username != null && !username.isBlank()) {
            return new StompPrincipal(userId, username);
        }

        // Fallback principal to avoid null principal at broker layer.
        return () -> "anonymous-" + UUID.randomUUID();
    }
}
