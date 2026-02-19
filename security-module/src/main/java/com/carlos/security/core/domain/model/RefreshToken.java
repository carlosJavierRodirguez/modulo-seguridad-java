package com.carlos.security.core.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@SuperBuilder
@EqualsAndHashCode(of = "id")
public class RefreshToken extends AuditableEntity{

    private final UUID id;
    private final String token;
    private final UUID userId;
    private final LocalDateTime expiryDate;
    private final boolean revoked;
    private final LocalDateTime revokedAt;
    private final String ipAddress;
    private final String userAgent;

    // Métodos de negocio
    public void validate() {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date cannot be null");
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public RefreshToken revoke() {
        return RefreshToken.builder()
                .id(this.id)
                .token(this.token)
                .userId(this.userId)
                .expiryDate(this.expiryDate)
                .createdAt(this.createdAt)
                .revoked(true)
                .revokedAt(LocalDateTime.now())
                .ipAddress(this.ipAddress)
                .userAgent(this.userAgent)
                .build();
    }

    public long getDaysUntilExpiry() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), expiryDate);
    }

}
