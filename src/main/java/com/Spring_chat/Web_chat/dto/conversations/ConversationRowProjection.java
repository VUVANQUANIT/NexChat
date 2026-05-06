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
    /** Native query timestamp runtime type varies by dialect (Instant/OffsetDateTime/Timestamp). */
    Object getConversationCreatedAt();

    Long getLastMessageId();
    String getLastMessageContent();
    String getLastMessageType();
    Long getLastMessageSenderId();
    String getSenderUsername();
    /** Native query timestamp runtime type varies by dialect (Instant/OffsetDateTime/Timestamp). */
    Object getLastMessageCreatedAt();
    Boolean getLastMessageIsDeleted();

    Long getUnreadCount();

    Long getOtherUserId();
    String getOtherUsername();
    String getOtherAvatarUrl();
    Boolean getIsOnline();
}
