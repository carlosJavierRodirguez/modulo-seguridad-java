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
public class Permission extends AuditableEntity {

    private final UUID id;
    private final String name;
    private final String description;
    private final String resource;
    private final PermissionType type;
    private final boolean active;

    // Métodos de negocio
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del permiso no puede ser nulo ni estar vacío");
        }
        if (resource == null || resource.trim().isEmpty()) {
            throw new IllegalArgumentException("El recurso de permiso no puede ser nulo o vacío");
        }
        if (type == null) {
            throw new IllegalArgumentException("El tipo de permiso no puede ser nulo");
        }
    }

    public String getFullPermissionName() {
        return String.format("%s:%s:%s", resource, type.name(), name);
    }

    public boolean canPerform(PermissionType requestedType) {
        return this.active && this.type.includes(requestedType);
    }

    // Enum para tipos de permisos
    public enum PermissionType {
        CREATE,
        READ,
        UPDATE,
        DELETE,
        EXECUTE,
        ADMIN;

        public boolean includes(PermissionType other) {
            if (this == ADMIN) {
                return true; // ADMIN incluye todos los permisos
            }
            return this == other;
        }
    }

}
