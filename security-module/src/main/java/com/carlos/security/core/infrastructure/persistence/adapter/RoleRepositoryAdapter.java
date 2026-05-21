package com.carlos.security.core.infrastructure.persistence.adapter;

import com.carlos.security.core.domain.model.RefreshToken;
import com.carlos.security.core.domain.model.Role;
import com.carlos.security.core.domain.repository.RoleRepository;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;
import com.carlos.security.core.infrastructure.persistence.entity.RoleEntity;
import com.carlos.security.core.infrastructure.persistence.mapper.RoleEntityMapper;
import com.carlos.security.core.infrastructure.persistence.repository.JpaRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final JpaRoleRepository jpaRepository;
    private final RoleEntityMapper mapper;

    // ========== Operaciones básicas ==========

    @Override
    public Page<Role> findAll(PageRequest pageRequest) {
        Pageable pageable = toSpringPageable(pageRequest);
        org.springframework.data.domain.Page<RoleEntity> springPage =
                jpaRepository.findAll(pageable);
        return toDomainPage(springPage, pageRequest);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Role save(Role role) {
        RoleEntity entity = mapper.toEntity(role);
        RoleEntity savedEntity = jpaRepository.save(entity);
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

    // ========== Búsquedas por nombre ==========

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    // ========== Búsquedas por estado ==========

    @Override
    public Page<Role> findByActive(boolean active, PageRequest pageRequest) {
        List<RoleEntity> entities = jpaRepository.findByActive(active);
        return toDomainPageFromList(entities, pageRequest);
    }

    // ========== Búsquedas de roles del sistema vs personalizados ==========

    @Override
    public Page<Role> findSystemRoles(PageRequest pageRequest) {
        List<RoleEntity> entities = jpaRepository.findSystemRoles();
        return toDomainPageFromList(entities, pageRequest);
    }

    @Override
    public Page<Role> findCustomRoles(PageRequest pageRequest) {
        List<RoleEntity> entities = jpaRepository.findCustomRoles();
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
    private Page<Role> toDomainPage(
            org.springframework.data.domain.Page<RoleEntity> springPage,
            PageRequest pageRequest) {

        List<Role> content = springPage.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return Page.of(content, pageRequest, springPage.getTotalElements());
    }

    /**
     * Convierte List a Page (cuando JPA retorna List en lugar de Page)
     */
    private Page<Role> toDomainPageFromList(
            List<RoleEntity> entities,
            PageRequest pageRequest) {

        List<Role> allRoles = entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        int start = pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getPageSize(), allRoles.size());

        List<Role> pageContent = start < allRoles.size()
                ? allRoles.subList(start, end)
                : List.of();

        return Page.of(pageContent, pageRequest, allRoles.size());
    }

}
