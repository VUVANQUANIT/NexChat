package com.Spring_chat.Web_chat.repository;

import com.Spring_chat.Web_chat.dto.message.MessageRowDTO;
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
    @Query("""
        SELECT new com.Spring_chat.Web_chat.dto.message.MessageRowDTO(
            m.id, m.conversation.id, m.content, m.type,
            r.id, m.isDeleted, m.isEdited, m.editedAt, m.createdAt,
            u.id, u.username, u.avatarUrl,
            ms.status
        )
        FROM Message m
        JOIN m.sender u
        LEFT JOIN m.replyTo r
        LEFT JOIN MessageStatus ms ON ms.message = m AND ms.user.id = :userId
        WHERE m.conversation.id = :convId
          AND NOT EXISTS (SELECT 1 FROM MessageHidden mh WHERE mh.message = m AND mh.user.id = :userId)
        ORDER BY m.createdAt DESC, m.id DESC
        """)
    List<MessageRowDTO> findLatestMessages(
            @Param("convId") Long convId,
            @Param("userId") Long userId,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("""
        SELECT new com.Spring_chat.Web_chat.dto.message.MessageRowDTO(
            m.id, m.conversation.id, m.content, m.type,
            r.id, m.isDeleted, m.isEdited, m.editedAt, m.createdAt,
            u.id, u.username, u.avatarUrl,
            ms.status
        )
        FROM Message m
        JOIN m.sender u
        LEFT JOIN m.replyTo r
        LEFT JOIN MessageStatus ms ON ms.message = m AND ms.user.id = :userId
        WHERE m.conversation.id = :convId
          AND m.createdAt <= :leftAt
          AND NOT EXISTS (SELECT 1 FROM MessageHidden mh WHERE mh.message = m AND mh.user.id = :userId)
        ORDER BY m.createdAt DESC, m.id DESC
        """)
    List<MessageRowDTO> findLatestMessagesBeforeLeftAt(
            @Param("convId") Long convId,
            @Param("userId") Long userId,
            @Param("leftAt") Instant leftAt,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("""
        SELECT new com.Spring_chat.Web_chat.dto.message.MessageRowDTO(
            m.id, m.conversation.id, m.content, m.type,
            r.id, m.isDeleted, m.isEdited, m.editedAt, m.createdAt,
            u.id, u.username, u.avatarUrl,
            ms.status
        )
        FROM Message m
        JOIN m.sender u
        LEFT JOIN m.replyTo r
        LEFT JOIN MessageStatus ms ON ms.message = m AND ms.user.id = :userId
        WHERE m.conversation.id = :convId
          AND (
              m.createdAt < :beforeCreatedAt
              OR (m.createdAt = :beforeCreatedAt AND m.id < :beforeId)
          )
          AND NOT EXISTS (SELECT 1 FROM MessageHidden mh WHERE mh.message = m AND mh.user.id = :userId)
        ORDER BY m.createdAt DESC, m.id DESC
        """)
    List<MessageRowDTO> findMessagesBefore(
            @Param("convId") Long convId,
            @Param("userId") Long userId,
            @Param("beforeCreatedAt") Instant beforeCreatedAt,
            @Param("beforeId") Long beforeId,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("""
        SELECT new com.Spring_chat.Web_chat.dto.message.MessageRowDTO(
            m.id, m.conversation.id, m.content, m.type,
            r.id, m.isDeleted, m.isEdited, m.editedAt, m.createdAt,
            u.id, u.username, u.avatarUrl,
            ms.status
        )
        FROM Message m
        JOIN m.sender u
        LEFT JOIN m.replyTo r
        LEFT JOIN MessageStatus ms ON ms.message = m AND ms.user.id = :userId
        WHERE m.conversation.id = :convId
          AND m.createdAt <= :leftAt
          AND (
              m.createdAt < :beforeCreatedAt
              OR (m.createdAt = :beforeCreatedAt AND m.id < :beforeId)
          )
          AND NOT EXISTS (SELECT 1 FROM MessageHidden mh WHERE mh.message = m AND mh.user.id = :userId)
        ORDER BY m.createdAt DESC, m.id DESC
        """)
    List<MessageRowDTO> findMessagesBeforeAndBeforeLeftAt(
            @Param("convId") Long convId,
            @Param("userId") Long userId,
            @Param("leftAt") Instant leftAt,
            @Param("beforeCreatedAt") Instant beforeCreatedAt,
            @Param("beforeId") Long beforeId,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("SELECT m.createdAt FROM Message m WHERE m.id = :id")
    Instant findCreatedAtById(@Param("id") Long id);

    /** Cursor anchor for pagination: must belong to this conversation or the native query breaks (beforeCreatedAt null → returns whole page again). */
    @Query("SELECT m.createdAt FROM Message m WHERE m.id = :id AND m.conversation.id = :conversationId")
    Optional<Instant> findCreatedAtByIdAndConversationId(@Param("id") Long id, @Param("conversationId") Long conversationId);

    @Query("""
            SELECT m.createdAt
            FROM Message m
            WHERE m.id = :id
              AND m.conversation.id = :conversationId
              AND m.createdAt <= :leftAt
            """)
    Optional<Instant> findCreatedAtByIdAndConversationIdBeforeLeftAt(
            @Param("id") Long id,
            @Param("conversationId") Long conversationId,
            @Param("leftAt") Instant leftAt
    );

    Optional<Message> findFirstByConversation_IdAndSender_IdAndClientMessageIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
            Long conversationId,
            Long senderId,
            String clientMessageId,
            Instant threshold
    );

    Optional<Message> findByIdAndConversation_Id(Long id, Long conversationId);
}
