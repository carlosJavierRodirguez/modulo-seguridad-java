package com.carlos.security.core.domain.model;

import com.carlos.security.core.domain.valueobject.Email;
import com.carlos.security.core.domain.valueobject.Password;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuperBuilder
@Getter
@EqualsAndHashCode(of = "id")
public class User extends Auditable {

    private final UUID id;
    private final String username;
    private final Email email;
    private final Password password;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;

    @Builder.Default
    private final Set<Role> roles = new HashSet<>();

    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime lastLoginAt;
    private final LocalDateTime passwordChangedAt;

    private final Integer failedLoginAttempts;
    private final LocalDateTime lockedUntil;

    // Métodos de negocio
    public void validate() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("El nombre de usuario debe tener entre 3 y 50 caracteres.");
        }
        if (email == null) {
            throw new IllegalArgumentException("El correo electrónico no puede ser nulo.");
        }
        if (password == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula");
        }

        email.validate();
        password.validate();
    }

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return username;
        }
        return String.format("%s %s",
                firstName != null ? firstName : "",
                lastName != null ? lastName : "").trim();
    }

    public void addRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(r -> r.getName().equals(roleName) && r.isActive());
    }

    public boolean hasPermission(String permissionName) {
        return roles.stream()
                .filter(Role::isActive)
                .anyMatch(r -> r.hasPermission(permissionName));
    }

    public Set<String> getRoleNames() {
        return roles.stream()
                .filter(Role::isActive)
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    public Set<Permission> getAllPermissions() {
        return roles.stream()
                .filter(Role::isActive)
                .flatMap(r -> r.getPermissions().stream())
                .filter(Permission::isActive)
                .collect(Collectors.toSet());
    }

    public boolean isAccountLocked() {
        if (!accountNonLocked) {
            return true;
        }
        if (lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil)) {
            return true;
        }
        return false;
    }

    public boolean canLogin() {
        return enabled
                && accountNonExpired
                && !isAccountLocked()
                && credentialsNonExpired;
    }

    public User incrementFailedLoginAttempts() {
        int newAttempts = (failedLoginAttempts != null ? failedLoginAttempts : 0) + 1;
        LocalDateTime newLockedUntil = null;
        boolean newLocked = accountNonLocked;

        // Bloquear después de 5 intentos fallidos
        if (newAttempts >= 5) {
            newLockedUntil = LocalDateTime.now().plusMinutes(30);
            newLocked = false;
        }

        return User.builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .password(this.password)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .phoneNumber(this.phoneNumber)
                .roles(this.roles)
                .enabled(this.enabled)
                .accountNonExpired(this.accountNonExpired)
                .accountNonLocked(newLocked)
                .credentialsNonExpired(this.credentialsNonExpired)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(this.lastLoginAt)
                .passwordChangedAt(this.passwordChangedAt)
                .createdBy(this.createdBy)
                .updatedBy(this.updatedBy)
                .failedLoginAttempts(newAttempts)
                .lockedUntil(newLockedUntil)
                .build();
    }

    public User resetFailedLoginAttempts() {
        return User.builder()
                .id(this.id)
                .username(this.username)
                .email(this.email)
                .password(this.password)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .phoneNumber(this.phoneNumber)
                .roles(this.roles)
                .enabled(this.enabled)
                .accountNonExpired(this.accountNonExpired)
                .accountNonLocked(true)
                .credentialsNonExpired(this.credentialsNonExpired)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .passwordChangedAt(this.passwordChangedAt)
                .createdBy(this.createdBy)
                .updatedBy(this.updatedBy)
                .failedLoginAttempts(0)
                .lockedUntil(null)
                .build();
    }

    public boolean needsPasswordChange(int daysValid) {
        if (passwordChangedAt == null) {
            return true;
        }
        LocalDateTime expiryDate = passwordChangedAt.plusDays(daysValid);
        return LocalDateTime.now().isAfter(expiryDate);
    }

}
