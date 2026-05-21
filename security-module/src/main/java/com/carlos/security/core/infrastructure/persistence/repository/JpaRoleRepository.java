package com.carlos.security.core.infrastructure.persistence.repository;

import com.carlos.security.core.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, UUID> {
    // ========== Métodos Derivados ==========

    /**
     * Busca un rol por su nombre
     * SQL: SELECT * FROM roles WHERE name = ?
     */
    Optional<RoleEntity> findByName(String name);

    /**
     * Verifica si existe un rol con ese nombre
     * SQL: SELECT COUNT(*) > 0 FROM roles WHERE name = ?
     */
    boolean existsByName(String name);

    /**
     * Busca roles activos o inactivos
     * SQL: SELECT * FROM roles WHERE active = ?
     */
    List<RoleEntity> findByActive(boolean active);

    /**
     * Busca roles cuyo nombre empieza con un prefijo
     * SQL: SELECT * FROM roles WHERE name LIKE 'prefix%'
     */
    List<RoleEntity> findByNameStartingWith(String prefix);

    // ========== Queries Personalizadas con JPQL ==========

    /**
     * Busca roles del sistema (empiezan con "ROLE_SYSTEM_")
     * <p>
     * Los roles del sistema son especiales y no se pueden eliminar.
     */
    @Query("SELECT r FROM RoleEntity r WHERE r.name LIKE 'ROLE_SYSTEM_%'")
    List<RoleEntity> findSystemRoles();

    /**
     * Busca roles personalizados (NO empiezan con "ROLE_SYSTEM_")
     * <p>
     * Los roles personalizados son creados por administradores.
     */
    @Query("SELECT r FROM RoleEntity r WHERE r.name NOT LIKE 'ROLE_SYSTEM_%'")
    List<RoleEntity> findCustomRoles();

    /**
     * Busca un rol con sus permisos cargados (evita N+1 problem)
     * <p>
     * JOIN FETCH: Carga los permisos en la misma query
     * Útil cuando necesitas acceder a los permisos inmediatamente
     */
    @Query("SELECT r FROM RoleEntity r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE r.id = :id")
    Optional<RoleEntity> findByIdWithPermissions(@Param("id") UUID id);

    /**
     * Busca todos los roles con sus permisos cargados
     * <p>
     * DISTINCT: Evita duplicados cuando un rol tiene múltiples permisos
     */
    @Query("SELECT DISTINCT r FROM RoleEntity r " +
            "LEFT JOIN FETCH r.permissions")
    List<RoleEntity> findAllWithPermissions();

    /**
     * Cuenta roles activos
     */
    long countByActive(boolean active);

}
