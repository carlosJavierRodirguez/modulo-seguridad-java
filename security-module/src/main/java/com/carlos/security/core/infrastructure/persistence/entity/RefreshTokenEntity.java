package com.carlos.security.core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_token", columnList = "token"),
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_user_revoked", columnList = "user_id, revoked")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_token", columnNames = "token")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Token único (UUID en formato String)
     */
    @Column(name = "token", unique = true, nullable = false, length = 255)
    private String token;

    /**
     * ID del usuario propietario del token
     * <p>
     * Usamos solo el UUID en lugar de @ManyToOne para:
     * - Evitar lazy loading issues
     * - Mantener la simplicidad
     * - Evitar cargas innecesarias del usuario
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Fecha y hora de expiración del token
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;


    /**
     * Fecha de creación del token
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Indica si el token fue revocado manualmente
     */
    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private Boolean revoked = false;

    /**
     * Fecha y hora en que se revocó el token
     */
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    /**
     * Dirección IP desde donde se generó el token (opcional)
     * IPv6 requiere hasta 45 caracteres
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User-Agent del navegador/app que generó el token (opcional)
     */
    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (revoked == null) revoked = false;
    }

}
