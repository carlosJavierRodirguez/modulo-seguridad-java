package com.carlos.security.core.infrastructure.persistence.repository;

import com.carlos.security.core.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para UserEntity
 * <p>
 * Spring Data genera automáticamente la implementación.
 * Solo necesitas definir las firmas de los métodos.
 * <p>
 * IMPORTANTE:
 * - Trabaja con UserEntity (JPA), NO con User (dominio)
 * - Los métodos derivados son interpretados por Spring Data
 * - Para queries complejas, usa @Query con JPQL
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    // ========== Métodos Derivados (Spring Data los implementa automáticamente) ==========

    /**
     * Busca usuario por username
     * <p>
     * Spring Data genera: SELECT * FROM users WHERE username = ?
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Busca usuario por email
     * <p>
     * Spring Data genera: SELECT * FROM users WHERE email = ?
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Verifica si existe un usuario con ese username
     * <p>
     * Spring Data genera: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con ese email
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios por estado enabled
     * <p>
     * Spring Data genera: SELECT * FROM users WHERE enabled = ?
     */
    List<UserEntity> findByEnabled(boolean enabled);

    // ========== Queries Personalizadas con JPQL ==========

    /**
     * Busca usuarios por nombre de rol
     * <p>
     * JPQL (Java Persistence Query Language):
     * - Usa nombres de ENTIDADES (UserEntity) no de tablas (users)
     * - Usa nombres de PROPIEDADES (roles) no de columnas (role_id)
     * <p>
     * JOIN FETCH: Carga los roles en la misma query (evita N+1 problem)
     * DISTINCT: Evita duplicados cuando hay múltiples roles
     */
    @Query("SELECT DISTINCT u FROM UserEntity u " +
            "LEFT JOIN FETCH u.roles r " +
            "WHERE r.name = :roleName")
    List<UserEntity> findByRoleName(@Param("roleName") String roleName);

    /**
     * Busca cuentas bloqueadas
     * <p>
     * Una cuenta está bloqueada si:
     * - accountNonLocked = false, O
     * - lockedUntil es futuro (el bloqueo temporal aún no expiró)
     */
    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.accountNonLocked = false " +
            "OR (u.lockedUntil IS NOT NULL AND u.lockedUntil > CURRENT_TIMESTAMP)")
    List<UserEntity> findLockedAccounts();

    /**
     * Busca usuarios por búsqueda de texto (username, email, nombre)
     * <p>
     * LOWER(): Búsqueda insensible a mayúsculas/minúsculas
     * CONCAT(): Une firstName y lastName
     * LIKE %...%: Búsqueda parcial
     */
    @Query("SELECT u FROM UserEntity u " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<UserEntity> searchUsers(@Param("searchTerm") String searchTerm);

    /**
     * Cuenta usuarios activos
     */
    long countByEnabled(boolean enabled);

    /**
     * Busca usuarios creados después de una fecha
     */
    List<UserEntity> findByCreatedAtAfter(LocalDateTime date);

}
