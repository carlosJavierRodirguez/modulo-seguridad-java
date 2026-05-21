package com.carlos.security.core.infrastructure.persistence.repository;

import com.carlos.security.core.domain.model.Permission;
import com.carlos.security.core.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para PermissionEntity
 * <p>
 * Spring Data implementa automáticamente los métodos derivados.
 * Trabaja con PermissionEntity (JPA), NO con Permission (dominio).
 */
@Repository
public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, UUID> {

    // ========== Métodos Derivados ==========

    /**
     * Busca un permiso por su nombre
     * SQL: SELECT * FROM permissions WHERE name = ?
     */
    Optional<PermissionEntity> findByName(String name);

    /**
     * Verifica si existe un permiso con ese nombre
     * SQL: SELECT COUNT(*) > 0 FROM permissions WHERE name = ?
     */
    boolean existsByName(String name);

    /**
     * Busca permisos activos o inactivos
     * SQL: SELECT * FROM permissions WHERE active = ?
     */
    List<PermissionEntity> findByActive(boolean active);

    /**
     * Busca permisos por recurso
     * SQL: SELECT * FROM permissions WHERE resource = ?
     * <p>
     * Ejemplo: findByResource("users") → [CREATE_USER, READ_USER, ...]
     */
    List<PermissionEntity> findByResource(String resource);

    /**
     * Busca permisos por tipo de operación
     * SQL: SELECT * FROM permissions WHERE type = ?
     * <p>
     * Ejemplo: findByType(PermissionType.DELETE) → Todos los permisos de eliminación
     */
    List<PermissionEntity> findByType(Permission.PermissionType type);

    /**
     * Busca UN permiso por combinación única de resource + type
     * SQL: SELECT * FROM permissions WHERE resource = ? AND type = ?
     * <p>
     * IMPORTANTE: Retorna Optional porque la combinación es única
     * <p>
     * Ejemplo: findByResourceAndType("users", CREATE) → Optional[CREATE_USER]
     */
    Optional<PermissionEntity> findByResourceAndType(
            String resource,
            Permission.PermissionType type
    );

    /**
     * Verifica si existe un permiso con esa combinación resource + type
     * SQL: SELECT COUNT(*) > 0 FROM permissions WHERE resource = ? AND type = ?
     * <p>
     * Usado para validar antes de crear permisos (evitar duplicados)
     */
    boolean existsByResourceAndType(
            String resource,
            Permission.PermissionType type
    );

    /**
     * Cuenta permisos activos
     */
    long countByActive(boolean active);

    // ========== Queries Personalizadas con JPQL ==========

    /**
     * Búsqueda de texto en nombre O descripción
     * <p>
     * LOWER(): Búsqueda insensible a mayúsculas/minúsculas
     * CONCAT('%', :searchTerm, '%'): Búsqueda parcial (contiene)
     * <p>
     * Ejemplo: searchByNameOrDescription("user")
     * Resultado: CREATE_USER, DELETE_USER, "Gestionar usuarios", etc.
     */
    @Query("SELECT p FROM PermissionEntity p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<PermissionEntity> searchByNameOrDescription(@Param("searchTerm") String searchTerm);

    /**
     * Busca permisos por múltiples recursos
     * <p>
     * Útil cuando necesitas permisos de varios recursos a la vez
     * <p>
     * Ejemplo: findByResourceIn(["users", "reports"])
     * Resultado: Todos los permisos de users y reports
     */
    @Query("SELECT p FROM PermissionEntity p WHERE p.resource IN :resources")
    List<PermissionEntity> findByResourceIn(@Param("resources") List<String> resources);

    /**
     * Busca permisos peligrosos (DELETE y ADMIN)
     * <p>
     * Útil para auditoría de permisos críticos
     */
    @Query("SELECT p FROM PermissionEntity p " +
            "WHERE p.type IN ('DELETE', 'ADMIN')")
    List<PermissionEntity> findDangerousPermissions();
}
