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
public class MessageCreatedEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageCreatedEvent event) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("event", "MESSAGE_NEW");
        payload.put("data", event.payload());

        String destination = "/topic/conversations/" + event.conversationId();
        messagingTemplate.convertAndSend(destination, (Object) payload);
        log.debug("MESSAGE_NEW pushed to destination={}", destination);
    }
}
