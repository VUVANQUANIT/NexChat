package com.Spring_chat.Spring_chat.dto.friendship;

import com.Spring_chat.Spring_chat.ENUM.FriendshipStatus;
import lombok.Data;

import java.time.Instant;
@Data
public class RejectFriendResponseDTO {
    private Long id;
    private FriendshipStatus status = FriendshipStatus.REJECTED;
    private Instant updatedAt;

}
