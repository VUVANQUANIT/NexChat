package com.Spring_chat.Spring_chat.dto.friendship;

import com.Spring_chat.Spring_chat.ENUM.FriendshipStatus;
import lombok.Data;

@Data
public class FriendResponseBlockDTO {
    private Long blockedUserId;
    private FriendshipStatus friendshipStatus = FriendshipStatus.BLOCKED;
}
