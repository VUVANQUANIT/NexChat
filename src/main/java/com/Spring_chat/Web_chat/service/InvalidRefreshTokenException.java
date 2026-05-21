package com.Spring_chat.Web_chat.service;

import com.Spring_chat.Web_chat.exception.ErrorCode;

import com.Spring_chat.Web_chat.exception.AppException;

public class InvalidRefreshTokenException extends AppException {

    public InvalidRefreshTokenException(String message) {
        super(ErrorCode.INVALID_REFRESH_TOKEN, message);
    }
}
