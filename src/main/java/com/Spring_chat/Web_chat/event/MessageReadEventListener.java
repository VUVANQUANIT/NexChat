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
@RequiredArgsConstructor
@Slf4j
public class MessageReadEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReadUpdated(MessageReadEvent event) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("conversationId", event.conversationId());
        data.put("userId", event.userId());
        data.put("lastReadMessageId", event.lastReadMessageId());
        data.put("readAt", event.readAt());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("event", "READ_RECEIPT");
        payload.put("data", data);

        String destination = "/topic/conversations/" + event.conversationId();
        messagingTemplate.convertAndSend(destination, (Object) payload);
        log.debug("READ_RECEIPT pushed to destination={}", destination);
    }
}
