package com.carlos.security.core.domain.repository;

import com.carlos.security.core.domain.model.User;
import com.carlos.security.core.domain.valueobject.Email;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    /**
     * Obtiene todos los usuarios paginados
     * <p>
     * CASO DE USO: Dashboard de administración con tabla de usuarios
     *
     * @param pageRequest Configuración de paginación y ordenamiento
     * @return Página de usuarios
     * <p>
     * EJEMPLO:
     * PageRequest request = PageRequest.of(0, 20, "username", SortDirection.ASC);
     * Page<User> users = repository.findAll(request);
     * // Retorna: usuarios 1-20 ordenados por username
     */
    Page<User> findAll(PageRequest pageRequest);

    /**
     * Busca usuarios por rol con paginación
     * <p>
     * CASO DE USO: "Listar todos los administradores" (puede ser una lista larga)
     *
     * @param roleName    Nombre del rol
     * @param pageRequest Configuración de paginación
     * @return Página de usuarios con ese rol
     */
    Page<User> findByRoleName(String roleName, PageRequest pageRequest);

    /**
     * Busca usuarios activos/inactivos con paginación
     *
     * @param enabled     true = activos, false = inactivos
     * @param pageRequest Configuración de paginación
     * @return Página de usuarios según estado
     */
    Page<User> findByEnabled(boolean enabled, PageRequest pageRequest);

    /**
     * Busca usuarios cuya cuenta está bloqueada (paginado)
     *
     * @param pageRequest Configuración de paginación
     * @return Página de usuarios bloqueados
     */
    Page<User> findLockedAccounts(PageRequest pageRequest);

    /**
     * Busca usuarios por búsqueda de texto en username, email, nombre
     * <p>
     * CASO DE USO: Barra de búsqueda en el dashboard
     *
     * @param searchTerm  Término de búsqueda
     * @param pageRequest Configuración de paginación
     * @return Página de usuarios que coinciden con la búsqueda
     * <p>
     * EJEMPLO:
     * repository.searchUsers("john", PageRequest.of(0, 10));
     * // Retorna usuarios con "john" en username, email o nombre
     */
    Page<User> searchUsers(String searchTerm, PageRequest pageRequest);

    /**
     * NOTA: Este método NO está paginado porque típicamente
     * un usuario tiene pocos roles (2-5), no necesita paginación
     */
    List<User> findByRoleNameNoPagination(String roleName);

    void deleteById(UUID id);

    long count();

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(Email email);

    boolean existsByUsername(String username);

    boolean existsByEmail(Email email);
}
