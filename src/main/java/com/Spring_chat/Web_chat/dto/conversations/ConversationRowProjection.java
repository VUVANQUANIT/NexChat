package com.Spring_chat.Web_chat.dto.conversations;

/**
 * Spring Data JPA projection for the conversation inbox native query.
 *
 * The getter types must match the native result mapping.
 */
public interface ConversationRowProjection {
    Long getId();
    String getType();
    String getTitle();
    String getAvatarUrl();
    /** Native timestamptz — Hibernate maps to Instant (UTC). */
    java.time.Instant getConversationCreatedAt();

    Long getLastMessageId();
    String getLastMessageContent();
    String getLastMessageType();
    Long getLastMessageSenderId();
    String getSenderUsername();
    /** Native timestamptz — Hibernate maps to Instant (UTC). */
    java.time.Instant getLastMessageCreatedAt();
    Boolean getLastMessageIsDeleted();

    Long getUnreadCount();

    Long getOtherUserId();
    String getOtherUsername();
    String getOtherAvatarUrl();
    Boolean getIsOnline();
}
