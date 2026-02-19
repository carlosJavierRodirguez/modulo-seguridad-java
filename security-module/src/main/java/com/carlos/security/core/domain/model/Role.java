package com.carlos.security.core.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@SuperBuilder
@EqualsAndHashCode(of = "id")
public class Role extends AuditableEntity {

    private final UUID id;
    private final String name;
    private final String description;

    @Builder.Default
    private final Set<Permission> permissions = new HashSet<>();

    private final boolean active;

    // Métodos de negocio
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
        if (!name.matches("^ROLE_[A-Z_]+$")) {
            throw new IllegalArgumentException("Role name must follow pattern: ROLE_XXXXX");
        }
    }

    public boolean hasPermission(String permissionName) {
        return permissions.stream()
                .anyMatch(p -> p.getName().equals(permissionName) && p.isActive());
    }

    public void addPermission(Permission permission) {
        if (permission == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public int getPermissionCount() {
        return permissions.size();
    }

    public boolean isSystemRole() {
        return name.startsWith("ROLE_SYSTEM_");
    }

}
