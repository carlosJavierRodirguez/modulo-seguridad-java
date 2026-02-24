package com.carlos.security.core.infrastructure.persistence.mapper;

import com.carlos.security.core.domain.model.Permission;
import com.carlos.security.core.domain.model.Role;
import com.carlos.security.core.infrastructure.persistence.entity.PermissionEntity;
import com.carlos.security.core.infrastructure.persistence.entity.RoleEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleEntityMapper {

    private final PermissionEntityMapper permissionMapper;

    // Constructor con inyección
    public RoleEntityMapper(PermissionEntityMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    public RoleEntity toEntity(Role domain) {
        if (domain == null) {
            return null;
        }

        return RoleEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .permissions(mapPermissionsToEntities(domain.getPermissions()))  // ← CLAVE
                .active(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    public Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }

        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .permissions(mapPermissionsToDomain(entity.getPermissions()))  // ← CLAVE
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    // ========== Métodos auxiliares para convertir colecciones ==========

    /**
     * Convierte Set de Permissions (dominio) a Set de PermissionEntities (JPA)
     */
    private Set<PermissionEntity> mapPermissionsToEntities(Set<Permission> permissions) {
        if (permissions == null) {
            return Set.of();  // ← Retorna Set vacío, NO null
        }
        return permissions.stream()
                .map(permissionMapper::toEntity)  // ← Usa el mapper inyectado
                .collect(Collectors.toSet());
    }

    /**
     * Convierte Set de PermissionEntities (JPA) a Set de Permissions (dominio)
     */
    private Set<Permission> mapPermissionsToDomain(Set<PermissionEntity> entities) {
        if (entities == null) {
            return Set.of();
        }
        return entities.stream()
                .map(permissionMapper::toDomain)  // ← Usa el mapper inyectado
                .collect(Collectors.toSet());
    }

}