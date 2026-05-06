package com.Spring_chat.Web_chat.dto.websocket;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WsTypingCommand {
    @NotNull(message = "conversationId is required")
    private Long conversationId;

    @NotNull(message = "isTyping is required")
    private Boolean isTyping;
}
