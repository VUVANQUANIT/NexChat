package com.Spring_chat.Web_chat.controller.realtime;

import com.Spring_chat.Web_chat.dto.ApiResponse;
import com.Spring_chat.Web_chat.dto.message.ReadReceiptRequestDTO;
import com.Spring_chat.Web_chat.dto.message.SendMessageResponseDTO;
import com.Spring_chat.Web_chat.dto.message.UpdateMessageRequestDTO;
import com.Spring_chat.Web_chat.dto.websocket.WsDeleteMessageCommand;
import com.Spring_chat.Web_chat.dto.websocket.WsEditMessageCommand;
import com.Spring_chat.Web_chat.dto.websocket.WsReadReceiptCommand;
import com.Spring_chat.Web_chat.dto.websocket.WsSendMessageCommand;
import com.Spring_chat.Web_chat.dto.websocket.WsTypingCommand;
import com.Spring_chat.Web_chat.exception.AppException;
import com.Spring_chat.Web_chat.exception.ErrorCode;
import com.Spring_chat.Web_chat.security.AuthenticatedUser;
import com.Spring_chat.Web_chat.service.message.MessageService;
import com.Spring_chat.Web_chat.websocket.StompPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Controller
@RequiredArgsConstructor
@Validated
@Slf4j
public class RealtimeMessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/messages.send")
    public void sendMessage(@Valid WsSendMessageCommand command, Principal principal) {
        runAsAuthenticatedUser(principal, () -> {
            ApiResponse<SendMessageResponseDTO> response = messageService.sendMessage(
                    command.getConversationId(),
                    command.getRequest()
            );
            log.debug("STOMP messages.send processed for conversationId={}", response.getData().getConversationId());
            return null;
        });
    }

    @MessageMapping("/messages.read")
    public void markAsRead(@Valid WsReadReceiptCommand command, Principal principal) {
        runAsAuthenticatedUser(principal, () -> {
            ReadReceiptRequestDTO request = new ReadReceiptRequestDTO();
            request.setLastReadMessageId(command.getLastReadMessageId());
            messageService.markAsRead(command.getConversationId(), request);
            return null;
        });
    }

    @MessageMapping("/messages.edit")
    public void editMessage(@Valid WsEditMessageCommand command, Principal principal) {
        runAsAuthenticatedUser(principal, () -> {
            UpdateMessageRequestDTO request = new UpdateMessageRequestDTO();
            request.setContent(command.getContent());
            messageService.updateMessage(command.getMessageId(), request);
            return null;
        });
    }

    @MessageMapping("/messages.delete")
    public void deleteMessage(@Valid WsDeleteMessageCommand command, Principal principal) {
        runAsAuthenticatedUser(principal, () -> {
            messageService.deleteMessage(command.getMessageId(), command.getScope());
            return null;
        });
    }

    @MessageMapping("/typing")
    public void typing(@Valid WsTypingCommand command, Principal principal) {
        runAsAuthenticatedUser(principal, () -> {
            StompPrincipal stompPrincipal = requireStompPrincipal(principal);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("conversationId", command.getConversationId());
            data.put("userId", stompPrincipal.userId());
            data.put("username", stompPrincipal.username());
            data.put("isTyping", command.getIsTyping());
            data.put("at", Instant.now());

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("event", "TYPING");
            payload.put("data", data);

            String destination = "/topic/typing/" + command.getConversationId();
            messagingTemplate.convertAndSend(destination, (Object) payload);
            return null;
        });
    }

    private <T> T runAsAuthenticatedUser(Principal principal, Supplier<T> action) {
        StompPrincipal stompPrincipal = requireStompPrincipal(principal);
        SecurityContext previous = SecurityContextHolder.getContext();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                stompPrincipal.userId(),
                stompPrincipal.username(),
                List.of()
        );
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                authenticatedUser.authorities()
        ));

        SecurityContextHolder.setContext(context);
        try {
            return action.get();
        } finally {
            SecurityContextHolder.setContext(previous);
        }
    }

    private StompPrincipal requireStompPrincipal(Principal principal) {
        if (principal instanceof StompPrincipal stompPrincipal) {
            return stompPrincipal;
        }
        throw new AppException(ErrorCode.UNAUTHORIZED, "WebSocket principal không hợp lệ");
    }
}
