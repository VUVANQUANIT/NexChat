package com.Spring_chat.Spring_chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "RefreshToken",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_refresh_token_hash", columnNames = {"tokenHash"})
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private User user;

    @Column(name = "\"tokenHash\"", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "\"expiresAt\"", nullable = false)
    private Instant expiresAt;

    @Column(name = "\"revokedAt\"")
    private Instant revokedAt;

    @Column(name = "\"replacedByTokenHash\"", length = 255)
    private String replacedByTokenHash;

    @Column(name = "\"createdByIp\"", length = 45)
    private String createdByIp;

    @Column(name = "\"userAgent\"", length = 255)
    private String userAgent;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private Instant createdAt;

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }
}
