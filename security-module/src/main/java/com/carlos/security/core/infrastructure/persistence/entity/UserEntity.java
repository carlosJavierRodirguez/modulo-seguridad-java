package com.carlos.security.core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_username", columnList = "username"),
                @Index(name = "idx_email", columnList = "email")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_email", columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    /**
     * Relación ManyToMany con roles
     * <p>
     * - EAGER: Carga roles inmediatamente (requerido para Spring Security)
     * - CascadeType.MERGE: Solo actualiza relaciones, no elimina roles
     * - Tabla intermedia: user_roles
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    @Builder.Default
    private Set<RoleEntity> roles = new HashSet<>();

    // ========== Estados de la cuenta ==========

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(name = "account_non_expired", nullable = false)
    @Builder.Default
    private Boolean accountNonExpired = true;

    @Column(name = "account_non_locked", nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    @Builder.Default
    private Boolean credentialsNonExpired = true;

    // ========== Campos de auditoría ==========

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;


    // ========== Control de acceso ==========

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

}