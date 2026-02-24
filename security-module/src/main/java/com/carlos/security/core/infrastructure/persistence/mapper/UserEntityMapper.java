package com.carlos.security.core.infrastructure.persistence.mapper;

import com.carlos.security.core.domain.model.Role;
import com.carlos.security.core.domain.model.User;
import com.carlos.security.core.domain.valueobject.Email;
import com.carlos.security.core.domain.valueobject.Password;
import com.carlos.security.core.infrastructure.persistence.entity.RoleEntity;
import com.carlos.security.core.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserEntityMapper {

    private final RoleEntityMapper roleMapper;

    //Constructor con inyección
    public UserEntityMapper(RoleEntityMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    public UserEntity toEntity(User domain) {

        if (domain == null) return null;

        return UserEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .email(domain.getEmail().getValue())
                .password(domain.getPassword().getValue())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .phoneNumber(domain.getPhoneNumber())
                .roles(mapRoleToEntities(domain.getRoles()))
                .enabled(domain.isEnabled())
                .accountNonExpired(domain.isAccountNonExpired())
                .accountNonLocked(domain.isAccountNonLocked())
                .credentialsNonExpired(domain.isCredentialsNonExpired())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .lastLoginAt(domain.getLastLoginAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .passwordChangedAt(domain.getPasswordChangedAt())
                .failedLoginAttempts(domain.getFailedLoginAttempts())
                .lockedUntil(domain.getLockedUntil())
                .build();
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(Email.of(entity.getEmail()))
                .password(Password.ofEncoded(entity.getPassword()))
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phoneNumber(entity.getPhoneNumber())
                .roles(mapRolesToDomain(entity.getRoles()))
                .enabled(entity.getEnabled())
                .accountNonExpired(entity.getAccountNonExpired())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .accountNonLocked(entity.getAccountNonLocked())
                .credentialsNonExpired(entity.getCredentialsNonExpired())
                .lastLoginAt(entity.getLastLoginAt())
                .passwordChangedAt(entity.getPasswordChangedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .lockedUntil(entity.getLockedUntil())
                .build();


    }

    // ========== Métodos auxiliares para convertir colecciones ==========

    /**
     * Convierte Set de User (dominio) a Set de UserEntity (JPA)
     */
    private Set<RoleEntity> mapRoleToEntities(Set<Role> roles) {

        if (roles == null) return Set.of(); // ← Retorna Set vacío, NO null

        return roles.stream()
                .map(roleMapper::toEntity) // ← Usa el mapper inyectado
                .collect(Collectors.toSet());

    }

    /**
     * Convierte Set de UserEntity (JPA) a Set de User (dominio)
     */
    private Set<Role> mapRolesToDomain(Set<RoleEntity> entities) {
        if (entities == null) return Set.of();

        return entities.stream()
                .map(roleMapper::toDomain)// ← Usa el mapper inyectado
                .collect(Collectors.toSet());
    }

}