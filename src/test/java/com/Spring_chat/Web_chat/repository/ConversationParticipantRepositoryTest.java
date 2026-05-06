package com.Spring_chat.Web_chat.repository;

import com.Spring_chat.Web_chat.dto.conversations.ConversationRowProjection;
import com.Spring_chat.Web_chat.entity.Conversation;
import com.Spring_chat.Web_chat.entity.ConversationParticipant;
import com.Spring_chat.Web_chat.entity.Message;
import com.Spring_chat.Web_chat.entity.User;
import com.Spring_chat.Web_chat.enums.ConversationType;
import com.Spring_chat.Web_chat.enums.MessageType;
import com.Spring_chat.Web_chat.enums.UserStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
class ConversationParticipantRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ConversationParticipantRepository repository;

    @Test
    @DisplayName("conversation chưa có message vẫn xuất hiện với sort key là conversation.createdAt")
    void findUserConversationsFirstPage_ShouldReturnConversationWithoutMessages() {
        User alice = persistUser("alice_no_msg");
        User bob = persistUser("bob_no_msg");
        Conversation conversation = persistConversation("No messages", alice);
        persistParticipant(conversation, alice);
        persistParticipant(conversation, bob);
        entityManager.flush();
        entityManager.clear();

        List<ConversationRowProjection> rows = repository.findUserConversationsFirstPage(
                alice.getId(),
                10,
                Instant.now().minus(5, ChronoUnit.MINUTES)
        );

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getId()).isEqualTo(conversation.getId());
        assertThat(rows.get(0).getLastMessageId()).isNull();
        assertThat(rows.get(0).getUnreadCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("unreadCount chỉ tính message sau lastReadMessage")
    void findUserConversationsFirstPage_ShouldCalculateUnreadAfterLastReadMessage() {
        User alice = persistUser("alice_unread");
        User bob = persistUser("bob_unread");
        Conversation conversation = persistConversation("Unread", alice);
        ConversationParticipant aliceParticipant = persistParticipant(conversation, alice);
        persistParticipant(conversation, bob);

        Instant baseTime = Instant.now().minus(1, ChronoUnit.DAYS);
        Message first = persistMessage(conversation, bob, "first", baseTime);
        persistMessage(conversation, bob, "second", baseTime.plus(1, ChronoUnit.HOURS));
        persistMessage(conversation, bob, "third", baseTime.plus(2, ChronoUnit.HOURS));
        aliceParticipant.setLastReadMessage(first);
        entityManager.flush();
        entityManager.clear();

        List<ConversationRowProjection> rows = repository.findUserConversationsFirstPage(
                alice.getId(),
                10,
                Instant.now().minus(5, ChronoUnit.MINUTES)
        );

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getUnreadCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("leftAt giới hạn lastMessage và unreadCount theo thời điểm user rời nhóm")
    void findUserConversationsFirstPage_ShouldRespectLeftAtVisibility() {
        User alice = persistUser("alice_left");
        User bob = persistUser("bob_left");
        Conversation conversation = persistConversation("Left", alice);
        ConversationParticipant aliceParticipant = persistParticipant(conversation, alice);
        persistParticipant(conversation, bob);

        Instant baseTime = Instant.now().minus(1, ChronoUnit.DAYS);
        Message beforeLeave = persistMessage(conversation, bob, "before leave", baseTime.plus(1, ChronoUnit.HOURS));
        persistMessage(conversation, bob, "after leave", baseTime.plus(3, ChronoUnit.HOURS));
        aliceParticipant.setLeftAt(baseTime.plus(2, ChronoUnit.HOURS));
        entityManager.flush();
        entityManager.clear();

        List<ConversationRowProjection> rows = repository.findUserConversationsFirstPage(
                alice.getId(),
                10,
                Instant.now().minus(5, ChronoUnit.MINUTES)
        );

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getLastMessageId()).isEqualTo(beforeLeave.getId());
        assertThat(rows.get(0).getUnreadCount()).isEqualTo(1L);
    }

    private User persistUser(String username) {
        User user = User.builder()
                .username(username)
                .email(username + "@example.com")
                .passwordHash("hash")
                .status(UserStatus.ACTIVE)
                .lastSeen(Instant.now())
                .build();
        entityManager.persist(user);
        return user;
    }

    private Conversation persistConversation(String title, User owner) {
        Conversation conversation = Conversation.builder()
                .type(ConversationType.GROUP)
                .title(title)
                .owner(owner)
                .build();
        entityManager.persist(conversation);
        return conversation;
    }

    private ConversationParticipant persistParticipant(Conversation conversation, User user) {
        ConversationParticipant participant = ConversationParticipant.builder()
                .conversation(conversation)
                .user(user)
                .build();
        entityManager.persist(participant);
        return participant;
    }

    private Message persistMessage(Conversation conversation, User sender, String content, Instant createdAt) {
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(content)
                .type(MessageType.TEXT)
                .isDeleted(false)
                .isEdited(false)
                .build();
        entityManager.persist(message);
        entityManager.flush();
        entityManager.createNativeQuery("UPDATE messages SET created_at = :createdAt WHERE id = :id")
                .setParameter("createdAt", createdAt)
                .setParameter("id", message.getId())
                .executeUpdate();
        return message;
    }
}
