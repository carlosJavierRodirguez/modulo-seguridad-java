package com.carlos.security.core.infrastructure.persistence.adapter;

import com.carlos.security.core.domain.model.User;
import com.carlos.security.core.domain.repository.UserRepository;
import com.carlos.security.core.domain.valueobject.Email;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;
import com.carlos.security.core.infrastructure.persistence.entity.UserEntity;
import com.carlos.security.core.infrastructure.persistence.mapper.UserEntityMapper;
import com.carlos.security.core.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaRepository;
    private final UserEntityMapper mapper;

    // ========== Operaciones básicas ==========

    @Override
    public Page<User> findAll(PageRequest pageRequest) {
        Pageable pageable = toSpringPageable(pageRequest);
        org.springframework.data.domain.Page<UserEntity> springPage =
                jpaRepository.findAll(pageable);
        return toDomainPage(springPage, pageRequest);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public User deleteById(UUID id) {
        jpaRepository.deleteById(id);
        return null;
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    // ========== Búsquedas por campo único ==========

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        // Email es Value Object, extraer el String
        return jpaRepository.findByEmail(email.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

// ========== Búsquedas por rol ==========

    @Override
    public Page<User> findByRoleName(String roleName, PageRequest pageRequest) {
        List<UserEntity> entities = jpaRepository.findByRoleName(roleName);
        return toDomainPageFromList(entities, pageRequest);
    }

    @Override
    public List<User> findByRoleNameNoPagination(String roleName) {
        List<UserEntity> entities = jpaRepository.findByRoleName(roleName);
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    // ========== Búsquedas por estado ==========

    @Override
    public Page<User> findByEnabled(boolean enabled, PageRequest pageRequest) {
        List<UserEntity> entities = jpaRepository.findByEnabled(enabled);
        return toDomainPageFromList(entities, pageRequest);
    }

    @Override
    public Page<User> findLockedAccounts(PageRequest pageRequest) {
        List<UserEntity> entities = jpaRepository.findLockedAccounts();
        return toDomainPageFromList(entities, pageRequest);
    }

    // ========== Búsqueda global ==========

    @Override
    public Page<User> searchUsers(String searchTerm, PageRequest pageRequest) {
        List<UserEntity> entities = jpaRepository.searchUsers(searchTerm);
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
    private Page<User> toDomainPage(
            org.springframework.data.domain.Page<UserEntity> springPage,
            PageRequest pageRequest) {

        // Convertir cada PermissionEntity → Permission
        List<User> content = springPage.getContent().stream()
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
    private Page<User> toDomainPageFromList(
            List<UserEntity> entities,
            PageRequest pageRequest) {

        // Convertir entidades a dominio
        List<User> allUser = entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        // Aplicar paginación manual
        int start = pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getPageSize(), allUser.size());

        List<User> pageContent = start < allUser.size()
                ? allUser.subList(start, end)
                : List.of();

        return Page.of(pageContent, pageRequest, allUser.size());
    }

}