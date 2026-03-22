package com.Spring_chat.Spring_chat.service.friendship;

import com.Spring_chat.Spring_chat.dto.ApiResponse;
import com.Spring_chat.Spring_chat.dto.friendship.FriendRequestCreateRequestDTO;
import com.Spring_chat.Spring_chat.dto.friendship.FriendRequestResponseDTO;

public interface FriendShipService {
    ApiResponse<FriendRequestResponseDTO> sendRequestFriend(FriendRequestCreateRequestDTO friendRequestCreateRequestDTO);

}
