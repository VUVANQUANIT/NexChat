package com.Spring_chat.Web_chat.dto.websocket;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WsReadReceiptCommand {
    @NotNull(message = "conversationId is required")
    private Long conversationId;

    @NotNull(message = "lastReadMessageId is required")
    private Long lastReadMessageId;
}
