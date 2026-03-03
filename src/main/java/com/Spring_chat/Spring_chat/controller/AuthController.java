package com.Spring_chat.Spring_chat.controller;

import com.Spring_chat.Spring_chat.dto.auth.LoginResponseDTO;
import com.Spring_chat.Spring_chat.dto.auth.RefreshRequestDTO;
import com.Spring_chat.Spring_chat.service.InvalidRefreshTokenException;
import com.Spring_chat.Spring_chat.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO request,
                                                    HttpServletRequest httpServletRequest) {
        LoginResponseDTO response = refreshTokenService.rotateRefreshTokenAndIssueAccessToken(
                request.getRefresh_token(),
                extractClientIp(httpServletRequest),
                httpServletRequest.getHeader("User-Agent")
        );
        return ResponseEntity.ok(response);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public Map<String, Object> handleInvalidRefreshToken(InvalidRefreshTokenException ex,
                                                         HttpServletRequest request) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "message", ex.getMessage(),
                "path", request.getRequestURI()
        );
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
