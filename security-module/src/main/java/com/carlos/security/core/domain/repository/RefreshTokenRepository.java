package com.carlos.security.core.domain.repository;

import com.carlos.security.core.domain.model.RefreshToken;
import com.carlos.security.core.domain.model.User;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {

    // ========== Operaciones básicas ==========
    Page<RefreshToken> findAll(PageRequest pageRequest);

    Optional<RefreshToken> findById(UUID id);

    RefreshToken save(RefreshToken refreshToken);

    void deleteById(UUID id);

    long count();

    // ========== Búsquedas por token ==========
    Optional<RefreshToken> findByToken(String token);

    boolean existsByToken(String token);

    // ========== Búsquedas por usuario ==========

    /**
     * Busca el token ACTIVO (no revocado, no expirado) de un usuario
     */
    Optional<RefreshToken> findActiveByUserId(UUID userId);

    /**
     * Lista TODOS los tokens de un usuario (histórico)
     */
    Page<RefreshToken> findAllByUserId(UUID userId, PageRequest pageRequest);

    /**
     * Cuenta tokens activos de un usuario
     */
    long countActiveByUserId(UUID userId);

    // ========== Búsquedas por estado ==========
    Page<RefreshToken> findByRevoked(boolean revoked, PageRequest pageRequest);

    /**
     * Busca tokens que ya expiraron
     */
    Page<RefreshToken> findExpiredTokens(PageRequest pageRequest);

    // ========== Operaciones de limpieza/revocación ==========

    /**
     * Revoca todos los tokens de un usuario
     * Caso de uso: "Cerrar sesión en todos los dispositivos"
     */
    void revokeAllByUserId(UUID userId);

    /**
     * Elimina tokens expirados de la BD
     * Caso de uso: Job de limpieza nocturna
     */
    void deleteExpiredTokens();

    /**
     * Elimina todos los tokens de un usuario
     * Caso de uso: Eliminación de cuenta
     */
    void deleteAllByUserId(UUID userId);
}