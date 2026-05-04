package com.Spring_chat.Web_chat.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Placeholder for realtime fan-out (e.g. STOMP {@code /topic/conversations/{id}}).
 * Keeps {@link com.Spring_chat.Web_chat.service.message.MessageService} free of transport concerns.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MessageEditedEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageEdited(MessageEditedEvent event) {
        Map<String, Object> editedBy = new LinkedHashMap<>();
        editedBy.put("id", event.editedByUserId());
        editedBy.put("username", event.editedByUsername());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", event.messageId());
        data.put("conversationId", event.conversationId());
        data.put("content", event.content());
        data.put("isEdited", event.isEdited());
        data.put("editedAt", event.editedAt());
        data.put("editedBy", editedBy);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("event", "MESSAGE_EDITED");
        payload.put("data", data);

        String destination = "/topic/conversations/" + event.conversationId();
        messagingTemplate.convertAndSend(destination, (Object) payload);

        log.info(
                "MESSAGE_EDITED messageId={} conversationId={} editedBy={}",
                event.messageId(),
                event.conversationId(),
                event.editedByUserId()
        );
        log.debug("MESSAGE_EDITED pushed to destination={}", destination);
    }
}
