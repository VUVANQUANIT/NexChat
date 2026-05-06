package com.Spring_chat.Web_chat.websocket;

import com.Spring_chat.Web_chat.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    public static final String ATTR_USER_ID = "wsUserId";
    public static final String ATTR_USERNAME = "wsUsername";

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        String token = extractQueryParam(request, "token");
        if (token == null || token.isBlank()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        Claims claims = jwtService.validateAndExtractClaims(token.trim());
        if (claims == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String username = claims.getSubject();
        Long userId = claims.get("uid", Long.class);
        if (username == null || username.isBlank() || userId == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        attributes.put(ATTR_USER_ID, userId);
        attributes.put(ATTR_USERNAME, username);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        if (exception != null) {
            log.debug("WebSocket handshake failed: {}", exception.getMessage());
        }
    }

    private String extractQueryParam(ServerHttpRequest request, String key) {
        String query = request.getURI() != null ? request.getURI().getRawQuery() : null;
        if (query == null || query.isBlank()) {
            return null;
        }

        String prefix = key + "=";
        for (String pair : query.split("&")) {
            if (pair.startsWith(prefix) && pair.length() > prefix.length()) {
                return pair.substring(prefix.length());
            }
        }
        return null;
    }
}
