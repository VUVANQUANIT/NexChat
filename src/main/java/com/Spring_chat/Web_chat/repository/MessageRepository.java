package com.Spring_chat.Web_chat.repository;

import com.Spring_chat.Web_chat.dto.message.MessageRowProjection;
import com.Spring_chat.Web_chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
            SELECT m FROM Message m
            JOIN FETCH m.conversation
            JOIN FETCH m.sender
            LEFT JOIN FETCH m.replyTo
            WHERE m.id = :id
            """)
    Optional<Message> findDetailedById(@Param("id") Long id);
    /**
     * First page — no cursor. Trả trang đầu (tin mới nhất) không cần tham số cursor.
     * Tách khỏi query cursor để tránh PostgreSQL lỗi "could not determine data type of parameter $N"
     * khi truyền null vào prepared statement.
     */
    @Query(value = """
        SELECT
            m.id, m.conversation_id as conversationId, m.content, m.type,
            m.reply_to_id as replyToId, m.is_deleted as isDeleted,
            m.is_edited as isEdited, m.edited_at as editedAt, m.created_at as createdAt,
            u.id as senderId, u.username as senderUsername, u.avatar_url as senderAvatar,
            ms.status as myStatus
        FROM messages m
        INNER JOIN users u ON m.sender_id = u.id
        LEFT JOIN message_statuses ms ON ms.message_id = m.id AND ms.user_id = :userId
        WHERE m.conversation_id = :convId
          AND NOT EXISTS (SELECT 1 FROM message_hidden mh WHERE mh.message_id = m.id AND mh.user_id = :userId)
        ORDER BY m.created_at DESC, m.id DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<MessageRowProjection> findLatestMessages(
            @Param("convId") Long convId,
            @Param("userId") Long userId,
            @Param("limit") int limit
    );

    /**
     * Cursor page — trả tin nhắn cũ hơn cursor (beforeCreatedAt + beforeId).
     * Cả hai tham số đều NOT NULL (được validate ở service trước khi gọi).
     */
    @Query(value = """
        SELECT
            m.id, m.conversation_id as conversationId, m.content, m.type,
            m.reply_to_id as replyToId, m.is_deleted as isDeleted,
            m.is_edited as isEdited, m.edited_at as editedAt, m.created_at as createdAt,
            u.id as senderId, u.username as senderUsername, u.avatar_url as senderAvatar,
            ms.status as myStatus
        FROM messages m
        INNER JOIN users u ON m.sender_id = u.id
        LEFT JOIN message_statuses ms ON ms.message_id = m.id AND ms.user_id = :userId
        WHERE m.conversation_id = :convId
          AND (
              m.created_at < :beforeCreatedAt
              OR (m.created_at = :beforeCreatedAt AND m.id < :beforeId)
          )
          AND NOT EXISTS (SELECT 1 FROM message_hidden mh WHERE mh.message_id = m.id AND mh.user_id = :userId)
        ORDER BY m.created_at DESC, m.id DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<MessageRowProjection> findMessagesBefore(
            @Param("convId") Long convId,
            @Param("userId") Long userId,
            @Param("beforeCreatedAt") Instant beforeCreatedAt,
            @Param("beforeId") Long beforeId,
            @Param("limit") int limit
    );

    @Query("SELECT m.createdAt FROM Message m WHERE m.id = :id")
    Instant findCreatedAtById(@Param("id") Long id);

    /** Cursor anchor for pagination: must belong to this conversation or the native query breaks (beforeCreatedAt null → returns whole page again). */
    @Query("SELECT m.createdAt FROM Message m WHERE m.id = :id AND m.conversation.id = :conversationId")
    Optional<Instant> findCreatedAtByIdAndConversationId(@Param("id") Long id, @Param("conversationId") Long conversationId);

    Optional<Message> findFirstByConversation_IdAndSender_IdAndClientMessageIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
            Long conversationId,
            Long senderId,
            String clientMessageId,
            Instant threshold
    );

    Optional<Message> findByIdAndConversation_Id(Long id, Long conversationId);
}
