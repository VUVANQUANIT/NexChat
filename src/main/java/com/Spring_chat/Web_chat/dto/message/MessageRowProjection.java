package com.Spring_chat.Web_chat.dto.message;

import com.Spring_chat.Web_chat.enums.MessageDeliveryStatus;
import com.Spring_chat.Web_chat.enums.MessageType;

public interface MessageRowProjection {
    Long getId();
    Long getConversationId();
    String getContent();
    MessageType getType();
    Long getReplyToId();
    Boolean getIsDeleted();
    Boolean getIsEdited();
    /** Native query timestamp runtime type varies by dialect (Instant/OffsetDateTime/Timestamp). */
    Object getEditedAt();
    Object getCreatedAt();
    
    // Sender Info
    Long getSenderId();
    String getSenderUsername();
    String getSenderAvatar();
    
    // My Status
    MessageDeliveryStatus getMyStatus();
}
