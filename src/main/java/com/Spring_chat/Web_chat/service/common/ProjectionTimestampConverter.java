package com.Spring_chat.Web_chat.service.common;

import com.Spring_chat.Web_chat.exception.AppException;
import com.Spring_chat.Web_chat.exception.ErrorCode;

import java.time.Instant;
import java.time.OffsetDateTime;

public final class ProjectionTimestampConverter {

    private ProjectionTimestampConverter() {
    }

    public static Instant toInstant(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toInstant();
        }
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toInstant();
        }
        if (value instanceof java.util.Date date) {
            return date.toInstant();
        }
        throw new AppException(ErrorCode.INTERNAL_ERROR, "Không thể chuyển đổi kiểu timestamp từ native query");
    }
}
