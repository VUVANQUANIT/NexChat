package com.Spring_chat.Web_chat.dto.websocket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WsEditMessageCommand {
    @NotNull(message = "messageId is required")
    private Long messageId;

    @NotBlank(message = "content is required")
    @Size(max = 4000, message = "content must be at most 4000 characters")
    private String content;
}
