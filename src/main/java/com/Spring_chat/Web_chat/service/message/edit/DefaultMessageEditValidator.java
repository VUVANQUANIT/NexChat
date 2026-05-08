package com.Spring_chat.Web_chat.service.message.edit;

import com.Spring_chat.Web_chat.entity.Message;
import com.Spring_chat.Web_chat.entity.User;
import com.Spring_chat.Web_chat.enums.MessageType;
import com.Spring_chat.Web_chat.exception.AppException;
import com.Spring_chat.Web_chat.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DefaultMessageEditValidator implements MessageEditValidator {

    @org.springframework.beans.factory.annotation.Value("${app.message.edit-window-minutes}")
    private int editWindowMinutes;

    private final Clock clock;

    @Override
    public void assertEditable(Message message, User editor, String normalizedNewContent) {
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(editor, "editor");
        Objects.requireNonNull(normalizedNewContent, "normalizedNewContent");

        if (!Objects.equals(message.getSender().getId(), editor.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN, "Chỉ người gửi mới được sửa tin nhắn");
        }
        if (Boolean.TRUE.equals(message.getIsDeleted())) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATED, "Không thể sửa tin nhắn đã xóa");
        }
        if (message.getType() != MessageType.TEXT) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATED, "Chỉ có thể sửa tin nhắn dạng TEXT");
        }
        Instant now = Instant.now(clock);
        Instant deadline = message.getCreatedAt().plus(Duration.ofMinutes(editWindowMinutes));
        if (now.isAfter(deadline)) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATED, "Hết thời gian cho phép sửa tin nhắn (" + editWindowMinutes + " phút)");
        }
        String current = message.getContent() == null ? "" : message.getContent().trim();
        if (current.equals(normalizedNewContent)) {
            throw new AppException(ErrorCode.BUSINESS_RULE_VIOLATED, "Nội dung mới phải khác nội dung hiện tại");
        }
    }
}
