package com.carlos.security.core.domain.exception;

/**
 * Excepción lanzada cuando las credenciales son inválidas durante el login
 */
public class InvalidCredentialsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Usuario o clave invalida";

    private final String username;

    public InvalidCredentialsException() {
        super(DEFAULT_MESSAGE);
        this.username = null;
    }

    public InvalidCredentialsException(String username) {
        super(String.format("Credenciales no válidas para el usuario: %s", username));
        this.username = username;
    }

    public InvalidCredentialsException(String username, String message) {
        super(message);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
