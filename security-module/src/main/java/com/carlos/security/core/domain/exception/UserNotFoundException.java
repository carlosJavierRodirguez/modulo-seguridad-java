package com.carlos.security.core.domain.exception;

import java.util.UUID;

/**
 * Excepción lanzada cuando no se encuentra un usuario
 */
public class UserNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Usuario no encontrado";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(UUID userId) {
        super(String.format("UUsuario no encontrado con ID: %s", userId));
    }

    public UserNotFoundException(String field, String value) {
        super(String.format("Usuario no encontrado con %s: %s", field, value));
    }

}
