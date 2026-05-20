package com.carlos.security.core.application.mapper;

import com.carlos.security.core.application.dto.response.RoleResponse;
import com.carlos.security.core.domain.model.Role;

import java.util.Set;
import java.util.stream.Collectors;

public final class RoleMapper {

    private RoleMapper() {}

    public static RoleResponse toResponse(Role role) {
        if (role == null) return null;

        // Extrae solo los nombres de los permisos activos — el cliente no necesita el objeto completo
        Set<String> permissionNames = role.getPermissions().stream()
                .filter(p -> p.isActive())
                .map(p -> p.getName())
                .collect(Collectors.toSet());

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissionNames)
                .active(role.isActive())
                .permissionCount(role.getPermissionCount())
                .build();
    }
}
