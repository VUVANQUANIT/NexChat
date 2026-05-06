package com.Spring_chat.Web_chat.dto.websocket;

import com.Spring_chat.Web_chat.dto.message.SendMessageRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WsSendMessageCommand {
    @NotNull(message = "conversationId is required")
    private Long conversationId;

    @NotNull(message = "request is required")
    @Valid
    private SendMessageRequestDTO request;
}
