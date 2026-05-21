package com.carlos.security.core.domain.repository;

import com.carlos.security.core.domain.model.Permission;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {

    // Operaciones básicas
    Page<Permission> findAll(PageRequest pageRequest);

    Optional<Permission> findById(UUID id);

    Permission save(Permission permission);

    void deleteById(UUID id);

    long count();

    // Búsquedas por campo único
    Optional<Permission> findByName(String name);

    boolean existsByName(String name);

    // Búsquedas por estado
    Page<Permission> findByActive(boolean active, PageRequest pageRequest);

    // Búsquedas por atributos de negocio
    Page<Permission> findByResource(String resource, PageRequest pageRequest);

    Page<Permission> findByType(Permission.PermissionType type, PageRequest pageRequest);

    // Búsqueda combinada (recurso + tipo)
    Optional<Permission> findByResourceAndType(String resource, Permission.PermissionType type);

    boolean existsByResourceAndType(String resource, Permission.PermissionType type);

    // Búsqueda global (nombre O descripción) - para barra de búsqueda
    Page<Permission> searchByNameOrDescription(String searchTerm, PageRequest pageRequest);
}