package com.carlos.security.core.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "El usuario ya existe";

    public UserAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public UserAlreadyExistsException(String field, String value) {
        super(String.format("Ya existe un usuario con %s: %s", field, value));
    }
}
