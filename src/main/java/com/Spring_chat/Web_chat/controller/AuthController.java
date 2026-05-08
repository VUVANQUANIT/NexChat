package com.Spring_chat.Web_chat.controller;

import com.Spring_chat.Web_chat.dto.auth.LoginRequestDTO;
import com.Spring_chat.Web_chat.dto.auth.LoginResponseDTO;
import com.Spring_chat.Web_chat.dto.auth.RefreshRequestDTO;
import com.Spring_chat.Web_chat.dto.auth.RegisterRequestDTO;
import com.Spring_chat.Web_chat.exception.AppException;
import com.Spring_chat.Web_chat.exception.ErrorCode;
import com.Spring_chat.Web_chat.security.AuthenticatedUser;
import com.Spring_chat.Web_chat.service.AuthService;
import com.Spring_chat.Web_chat.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Only trust X-Forwarded-For when the app is running behind a known reverse proxy.
     * Default: false (safe for local dev and environments without a proxy).
     * Set TRUST_PROXY=true in production when behind nginx/ELB/Cloudflare.
     */
    @Value("${app.security.trust-proxy:false}")
    private boolean trustProxy;

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    // ─── Register ─────────────────────────────────────────────────────────────

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDTO register(@Valid @RequestBody RegisterRequestDTO dto,
                                     HttpServletRequest request) {
        return authService.register(dto, extractClientIp(request), request.getHeader("User-Agent"));
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto,
                                  HttpServletRequest request) {
        return authService.login(dto, extractClientIp(request), request.getHeader("User-Agent"));
    }

    // ─── Refresh ──────────────────────────────────────────────────────────────

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO dto,
                                                    HttpServletRequest request) {
        LoginResponseDTO response = refreshTokenService.rotateRefreshTokenAndIssueAccessToken(
                dto.getRefresh_token(),
                extractClientIp(request),
                request.getHeader("User-Agent")
        );
        return ResponseEntity.ok(response);
    }

    // ─── Logout ───────────────────────────────────────────────────────────────

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal AuthenticatedUser principal) {
        if (principal == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Chưa đăng nhập hoặc token không hợp lệ");
        }
        authService.logout(principal.id());
    }

    private String extractClientIp(HttpServletRequest request) {
        if (trustProxy) {
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                return xff.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
