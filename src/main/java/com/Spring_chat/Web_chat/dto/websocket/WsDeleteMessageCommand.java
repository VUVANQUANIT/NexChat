package com.Spring_chat.Web_chat.dto.websocket;

import com.Spring_chat.Web_chat.enums.MessageDeleteScope;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WsDeleteMessageCommand {
    @NotNull(message = "messageId is required")
    private Long messageId;

    @NotNull(message = "scope is required")
    private MessageDeleteScope scope;
}
