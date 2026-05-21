package com.carlos.security.core.infrastructure.persistence.adapter;

import com.carlos.security.core.domain.model.Permission;
import com.carlos.security.core.domain.repository.PermissionRepository;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;
import com.carlos.security.core.infrastructure.persistence.entity.PermissionEntity;
import com.carlos.security.core.infrastructure.persistence.mapper.PermissionEntityMapper;
import com.carlos.security.core.infrastructure.persistence.repository.JpaPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter que implementa PermissionRepository (dominio)
 * usando JpaPermissionRepository (Spring Data JPA)
 * <p>
 * PATRÓN ADAPTER:
 * - Adapta la tecnología JPA para cumplir el contrato del dominio
 * - Convierte entre Permission (dominio) y PermissionEntity (JPA)
 * <p>
 * RESPONSABILIDADES:
 * 1. Delegar operaciones a JpaPermissionRepository
 * 2. Convertir PageRequest (dominio) → Pageable (Spring)
 * 3. Convertir Page (Spring) → Page (dominio)
 * 4. Convertir PermissionEntity ↔ Permission usando mapper
 */
@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final JpaPermissionRepository jpaRepository;
    private final PermissionEntityMapper mapper;

    // ========== Operaciones básicas ==========
    @Override
    public Page<Permission> findAll(PageRequest pageRequest) {
        // 1. Convertir PageRequest (dominio) → Pageable (Spring)
        Pageable pageable = toSpringPageable(pageRequest);

        // 2. Obtener página de entidades JPA
        org.springframework.data.domain.Page<PermissionEntity> springPage =
                jpaRepository.findAll(pageable);

        // 3. Convertir Page<PermissionEntity> → Page<Permission>
        return toDomainPage(springPage, pageRequest);
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);  // ← PermissionEntity → Permission
    }

    @Override
    public Permission save(Permission permission) {
        // 1. Convertir Permission → PermissionEntity
        PermissionEntity entity = mapper.toEntity(permission);

        // 2. Guardar en BD
        PermissionEntity savedEntity = jpaRepository.save(entity);

        // 3. Convertir PermissionEntity → Permission
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    // ========== Búsquedas por campo único ==========

    @Override
    public Optional<Permission> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    // ========== Búsquedas por estado ==========

    @Override
    public Page<Permission> findByActive(boolean active, PageRequest pageRequest) {
        Pageable pageable = toSpringPageable(pageRequest);

        // Spring Data NO tiene paginación automática para métodos derivados con List
        // Opción 1: Cambiar JpaRepository para retornar Page
        // Opción 2: Convertir List → Page manualmente

        List<PermissionEntity> entities = jpaRepository.findByActive(active);

        return toDomainPageFromList(entities, pageRequest);
    }

    // ========== Búsquedas por atributos de negocio ==========

    @Override
    public Page<Permission> findByResource(String resource, PageRequest pageRequest) {
        List<PermissionEntity> entities = jpaRepository.findByResource(resource);
        return toDomainPageFromList(entities, pageRequest);
    }

    @Override
    public Page<Permission> findByType(Permission.PermissionType type, PageRequest pageRequest) {
        List<PermissionEntity> entities = jpaRepository.findByType(type);
        return toDomainPageFromList(entities, pageRequest);
    }

    // ========== Búsqueda combinada ==========

    @Override
    public Optional<Permission> findByResourceAndType(String resource, Permission.PermissionType type) {
        return jpaRepository.findByResourceAndType(resource, type)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByResourceAndType(String resource, Permission.PermissionType type) {
        return jpaRepository.existsByResourceAndType(resource, type);
    }

    // ========== Búsqueda global ==========

    @Override
    public Page<Permission> searchByNameOrDescription(String searchTerm, PageRequest pageRequest) {
        List<PermissionEntity> entities = jpaRepository.searchByNameOrDescription(searchTerm);
        return toDomainPageFromList(entities, pageRequest);
    }

    // ========== Métodos de conversión (utilidades privadas) ==========

    /**
     * Convierte PageRequest (dominio) a Pageable (Spring)
     */
    private Pageable toSpringPageable(PageRequest pageRequest) {
        org.springframework.data.domain.Sort.Direction direction =
                pageRequest.getSortDirection() == PageRequest.SortDirection.ASC
                        ? org.springframework.data.domain.Sort.Direction.ASC
                        : org.springframework.data.domain.Sort.Direction.DESC;

        if (pageRequest.getSortBy() != null) {
            return org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPageNumber(),
                    pageRequest.getPageSize(),
                    org.springframework.data.domain.Sort.by(direction, pageRequest.getSortBy())
            );
        }

        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }

    /**
     * Convierte Page de Spring a Page de dominio
     */
    private Page<Permission> toDomainPage(
            org.springframework.data.domain.Page<PermissionEntity> springPage,
            PageRequest pageRequest) {

        // Convertir cada PermissionEntity → Permission
        List<Permission> content = springPage.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        // Crear Page del dominio
        return Page.of(content, pageRequest, springPage.getTotalElements());
    }

    /**
     * Convierte List a Page (cuando JPA retorna List en lugar de Page)
     * <p>
     * IMPORTANTE: Esto carga TODOS los registros en memoria
     * Solo usar cuando se sabe que la lista es pequeña
     */
    private Page<Permission> toDomainPageFromList(
            List<PermissionEntity> entities,
            PageRequest pageRequest) {

        // Convertir entidades a dominio
        List<Permission> allPermissions = entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        // Aplicar paginación manual
        int start = pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getPageSize(), allPermissions.size());

        List<Permission> pageContent = start < allPermissions.size()
                ? allPermissions.subList(start, end)
                : List.of();

        return Page.of(pageContent, pageRequest, allPermissions.size());
    }

}