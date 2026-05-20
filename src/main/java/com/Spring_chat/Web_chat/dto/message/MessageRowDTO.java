package com.Spring_chat.Web_chat.dto.message;

import com.Spring_chat.Web_chat.enums.MessageDeliveryStatus;
import com.Spring_chat.Web_chat.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class MessageRowDTO {
    private Long id;
    private Long conversationId;
    private String content;
    private MessageType type;
    private Long replyToId;
    private Boolean isDeleted;
    private Boolean isEdited;
    private Instant editedAt;
    private Instant createdAt;
    
    // Sender Info
    private Long senderId;
    private String senderUsername;
    private String senderAvatar;
    
    // My Status
    private MessageDeliveryStatus myStatus;
}
