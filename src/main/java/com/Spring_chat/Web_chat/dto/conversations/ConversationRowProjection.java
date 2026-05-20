package com.Spring_chat.Web_chat.dto.conversations;

import java.time.OffsetDateTime;

/**
 * Spring Data JPA projection for the conversation inbox native query.
 *
 * Getters for timestamp columns use OffsetDateTime because Hibernate maps
 * native-query timestamptz results to OffsetDateTime (not Instant) when
 * using interface projections.  The service layer calls .toInstant() before
 * passing values to DTOs (see GEMINI.md §7).
 */
public interface ConversationRowProjection {
    Long getId();
    String getType();
    String getTitle();
    String getAvatarUrl();
    /** timestamptz column — convert to Instant in service via .toInstant(). */
    OffsetDateTime getConversationCreatedAt();

    Long getLastMessageId();
    String getLastMessageContent();
    String getLastMessageType();
    Long getLastMessageSenderId();
    String getSenderUsername();
    /** timestamptz column — convert to Instant in service via .toInstant(). */
    OffsetDateTime getLastMessageCreatedAt();
    Boolean getLastMessageIsDeleted();

    Long getUnreadCount();

    Long getOtherUserId();
    String getOtherUsername();
    String getOtherAvatarUrl();
    Boolean getIsOnline();
}
