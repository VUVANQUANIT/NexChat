package com.Spring_chat.Web_chat.service.conversation;

import com.Spring_chat.Web_chat.dto.ApiResponse;
import com.Spring_chat.Web_chat.dto.conversations.*;
import org.springframework.data.domain.Pageable;



public interface ConversationService {
    ApiResponse<CreateConversationsResponseDTO> createConversation(CreateConversationsDTO createConversationsDTO);
    ApiResponse<ConversationListDTO>  getUserConversation(Pageable pageable, String cursor);
    ApiResponse<ConversationDetailDTO> getConversationDetail(Long conversationId);
    ApiResponse<UpdateConversationDTO> updateConversation(Long id, UpdateConversationDTO updateConversationDTO);
    ApiResponse<AddParticipantsResponseDTO> addUserToConversation(Long conversationId, AddParticipantsRequestDTO addParticipantsRequestDTO);
    ApiResponse<Void> removeParticipantFromConversation(Long conversationId, Long userId);
    boolean isOwner(Long conversationId);

    /**
     * Tự động tạo hoặc lấy PRIVATE conversation giữa 2 user sau khi accept friend.
     * Chạy trong REQUIRES_NEW để tránh lây nhiễm rollback-only sang transaction của acceptFriend.
     */
    void autoCreatePrivateConversation(Long userId1, Long userId2);
}
