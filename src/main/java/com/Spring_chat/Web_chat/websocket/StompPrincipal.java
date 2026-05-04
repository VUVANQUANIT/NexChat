package com.Spring_chat.Web_chat.websocket;

import java.security.Principal;

/**
 * Principal bound to a WebSocket session after JWT handshake validation.
 */
public record StompPrincipal(Long userId, String username) implements Principal {

    @Override
    public String getName() {
        return username;
    }
}
