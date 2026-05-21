package com.carlos.security.core.domain.exception;

import java.time.LocalDateTime;

/**
 * Excepción lanzada cuando un token ha expirado
 */
public class TokenExpiredException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Token has expired";

    private final LocalDateTime expiredAt;

    public TokenExpiredException() {
        super(DEFAULT_MESSAGE);
        this.expiredAt = null;
    }

    public TokenExpiredException(LocalDateTime expiredAt) {
        super(String.format("Token expired at: %s", expiredAt));
        this.expiredAt = expiredAt;
    }

    public TokenExpiredException(String tokenType, LocalDateTime expiredAt) {
        super(String.format("%s token expired at: %s", tokenType, expiredAt));
        this.expiredAt = expiredAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

}
