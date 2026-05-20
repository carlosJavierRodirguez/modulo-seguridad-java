package com.carlos.security.core.domain.exception;

public class TokenNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Token no encontrado";

    public TokenNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
