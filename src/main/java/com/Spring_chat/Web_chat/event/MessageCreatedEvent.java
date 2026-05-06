package com.Spring_chat.Web_chat.event;

import com.Spring_chat.Web_chat.dto.message.SendMessageResponseDTO;

/**
 * Published after a message is created so realtime transport can fan out
 * without coupling business logic to STOMP infrastructure.
 */
public record MessageCreatedEvent(
        long conversationId,
        SendMessageResponseDTO payload
) {}
