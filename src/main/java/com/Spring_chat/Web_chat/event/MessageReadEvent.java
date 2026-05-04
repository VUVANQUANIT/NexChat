package com.Spring_chat.Web_chat.event;

import java.time.Instant;

/**
 * Published after read pointer/status updates are committed.
 */
public record MessageReadEvent(
        long conversationId,
        long userId,
        long lastReadMessageId,
        Instant readAt
) {}
