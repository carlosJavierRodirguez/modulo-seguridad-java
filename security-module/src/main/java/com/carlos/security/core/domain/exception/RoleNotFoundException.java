package com.carlos.security.core.domain.exception;

import java.util.UUID;

public class RoleNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Rol no encontrado";

    public RoleNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public RoleNotFoundException(UUID roleId) {
        super(String.format("Rol no encontrado con ID: %s", roleId));
    }

    public RoleNotFoundException(String name) {
        super(String.format("Rol no encontrado con nombre: %s", name));
    }
}
