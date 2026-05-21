package com.carlos.security.core.infrastructure.persistence.repository;

import com.carlos.security.core.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para RefreshTokenEntity
 * <p>
 * Gestiona tokens de renovación para autenticación JWT.
 * Trabaja con RefreshTokenEntity (JPA), NO con RefreshToken (dominio).
 */
@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    // ========== Métodos Derivados ==========

    /**
     * Busca un token por su valor único
     * SQL: SELECT * FROM refresh_tokens WHERE token = ?
     * <p>
     * ESTE ES EL MÉTODO MÁS IMPORTANTE
     * Se usa para validar el token enviado por el cliente
     */
    Optional<RefreshTokenEntity> findByToken(String token);

    /**
     * Verifica si existe un token
     * SQL: SELECT COUNT(*) > 0 FROM refresh_tokens WHERE token = ?
     */
    boolean existsByToken(String token);

    /**
     * Busca todos los tokens de un usuario (histórico)
     * SQL: SELECT * FROM refresh_tokens WHERE user_id = ?
     * <p>
     * Un usuario puede tener múltiples tokens (varios dispositivos)
     */
    List<RefreshTokenEntity> findByUserId(UUID userId);

    /**
     * Busca tokens revocados o activos
     * SQL: SELECT * FROM refresh_tokens WHERE revoked = ?
     */
    List<RefreshTokenEntity> findByRevoked(Boolean revoked);

    /**
     * Busca tokens que expiran ANTES de una fecha (tokens expirados)
     * SQL: SELECT * FROM refresh_tokens WHERE expiry_date < ?
     * <p>
     * Ejemplo: findByExpiryDateBefore(LocalDateTime.now())
     * Retorna todos los tokens que ya expiraron
     */
    List<RefreshTokenEntity> findByExpiryDateBefore(LocalDateTime date);

    /**
     * Busca tokens que expiran DESPUÉS de una fecha (tokens aún válidos)
     * SQL: SELECT * FROM refresh_tokens WHERE expiry_date > ?
     */
    List<RefreshTokenEntity> findByExpiryDateAfter(LocalDateTime date);

    /**
     * Cuenta tokens de un usuario
     */
    long countByUserId(UUID userId);

    /**
     * Elimina todos los tokens de un usuario
     * SQL: DELETE FROM refresh_tokens WHERE user_id = ?
     * <p>
     * Caso de uso: Cuando se elimina una cuenta
     *
     * @Modifying indica que es una query de modificación (no de lectura)
     */
    @Modifying
    void deleteByUserId(UUID userId);

    // ========== Queries Personalizadas con JPQL ==========

    /**
     * Busca el token ACTIVO de un usuario
     * <p>
     * Condiciones para que un token sea válido:
     * 1. Pertenece al usuario
     * 2. NO está revocado
     * 3. NO ha expirado (expiryDate > ahora)
     * <p>
     * Retorna Optional porque solo debería haber 1 token activo por usuario
     * (aunque en la práctica puede haber varios por diferentes dispositivos)
     */
    @Query("SELECT rt FROM RefreshTokenEntity rt " +
            "WHERE rt.userId = :userId " +
            "AND rt.revoked = false " +
            "AND rt.expiryDate > :now " +
            "ORDER BY rt.createdAt DESC")
    Optional<RefreshTokenEntity> findActiveByUserId(
            @Param("userId") UUID userId,
            @Param("now") LocalDateTime now
    );

    /**
     * Busca TODOS los tokens activos de un usuario
     * <p>
     * Útil cuando un usuario tiene múltiples sesiones activas
     * (laptop, móvil, tablet)
     */
    @Query("SELECT rt FROM RefreshTokenEntity rt " +
            "WHERE rt.userId = :userId " +
            "AND rt.revoked = false " +
            "AND rt.expiryDate > :now")
    List<RefreshTokenEntity> findAllActiveByUserId(
            @Param("userId") UUID userId,
            @Param("now") LocalDateTime now
    );

    /**
     * Busca tokens expirados
     * <p>
     * Para job de limpieza automática
     */
    @Query("SELECT rt FROM RefreshTokenEntity rt " +
            "WHERE rt.expiryDate < CURRENT_TIMESTAMP")
    List<RefreshTokenEntity> findExpiredTokens();

    /**
     * Elimina tokens expirados
     * <p>
     * Job de limpieza: se ejecuta periódicamente (ej: cada noche)
     *
     * @Modifying indica que modifica datos (no solo lee)
     */
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt " +
            "WHERE rt.expiryDate < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

    /**
     * Revoca (marca como revocado) todos los tokens de un usuario
     * <p>
     * Caso de uso: "Cerrar sesión en todos los dispositivos"
     * <p>
     * No elimina los tokens (para auditoría), solo los marca como revocados
     */
    @Modifying
    @Query("UPDATE RefreshTokenEntity rt " +
            "SET rt.revoked = true, rt.revokedAt = CURRENT_TIMESTAMP " +
            "WHERE rt.userId = :userId " +
            "AND rt.revoked = false")
    void revokeAllByUserId(@Param("userId") UUID userId);

    /**
     * Cuenta tokens activos de un usuario
     * <p>
     * Útil para limitar el número de sesiones simultáneas
     * Ejemplo: "Máximo 5 dispositivos conectados"
     */
    @Query("SELECT COUNT(rt) FROM RefreshTokenEntity rt " +
            "WHERE rt.userId = :userId " +
            "AND rt.revoked = false " +
            "AND rt.expiryDate > CURRENT_TIMESTAMP")
    long countActiveByUserId(@Param("userId") UUID userId);
}