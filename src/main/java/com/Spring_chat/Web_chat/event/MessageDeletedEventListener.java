package com.Spring_chat.Web_chat.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageDeletedEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageDeleted(MessageDeletedEvent event) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", event.messageId());
        data.put("conversationId", event.conversationId());
        data.put("deletedAt", event.deletedAt());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("event", "MESSAGE_DELETED");
        payload.put("data", data);

        String destination = "/topic/conversations/" + event.conversationId();
        messagingTemplate.convertAndSend(destination, (Object) payload);

        log.info(
                "MESSAGE_DELETED messageId={} conversationId={} deletedBy={}",
                event.messageId(),
                event.conversationId(),
                event.deletedByUserId()
        );
        log.debug("MESSAGE_DELETED pushed to destination={}", destination);
    }
}
