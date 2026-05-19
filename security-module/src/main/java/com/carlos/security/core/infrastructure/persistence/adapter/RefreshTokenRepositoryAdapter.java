package com.carlos.security.core.infrastructure.persistence.adapter;

import com.carlos.security.core.domain.model.RefreshToken;
import com.carlos.security.core.domain.repository.RefreshTokenRepository;
import com.carlos.security.core.domain.valueobject.Page;
import com.carlos.security.core.domain.valueobject.PageRequest;
import com.carlos.security.core.infrastructure.persistence.entity.RefreshTokenEntity;
import com.carlos.security.core.infrastructure.persistence.mapper.RefreshTokenEntityMapper;
import com.carlos.security.core.infrastructure.persistence.repository.JpaRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRepository;
    private final RefreshTokenEntityMapper mapper;

    // ========== Operaciones básicas ==========

    @Override
    public Page<RefreshToken> findAll(PageRequest pageRequest) {
        Pageable pageable = toSpringPageable(pageRequest);
        org.springframework.data.domain.Page<RefreshTokenEntity> springPage =
                jpaRepository.findAll(pageable);
        return toDomainPage(springPage, pageRequest);
    }

    @Override
    public Optional<RefreshToken> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = mapper.toEntity(refreshToken);
        RefreshTokenEntity savedEntity = jpaRepository.save(entity);
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

    // ========== Búsquedas por token ==========

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByToken(String token) {
        return jpaRepository.existsByToken(token);
    }

    // ========== Búsquedas por usuario ==========

    @Override
    public Optional<RefreshToken> findActiveByUserId(UUID userId) {
        return jpaRepository.findActiveByUserId(userId, LocalDateTime.now())
                .map(mapper::toDomain);
    }

    @Override
    public Page<RefreshToken> findAllByUserId(UUID userId, PageRequest pageRequest) {
        List<RefreshTokenEntity> entities = jpaRepository.findByUserId(userId);
        return toDomainPageFromList(entities, pageRequest);
    }

    @Override
    public long countActiveByUserId(UUID userId) {
        return jpaRepository.countActiveByUserId(userId);
    }

    // ========== Búsquedas por estado ==========

    @Override
    public Page<RefreshToken> findByRevoked(boolean revoked, PageRequest pageRequest) {
        List<RefreshTokenEntity> entities = jpaRepository.findByRevoked(revoked);
        return toDomainPageFromList(entities, pageRequest);
    }

    @Override
    public Page<RefreshToken> findExpiredTokens(PageRequest pageRequest) {
        List<RefreshTokenEntity> entities = jpaRepository.findExpiredTokens();
        return toDomainPageFromList(entities, pageRequest);
    }

    // ========== Operaciones de limpieza/revocación ==========

    @Override
    public void revokeAllByUserId(UUID userId) {
        jpaRepository.revokeAllByUserId(userId);
    }

    @Override
    public void deleteExpiredTokens() {
        jpaRepository.deleteExpiredTokens();
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
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
    private Page<RefreshToken> toDomainPage(
            org.springframework.data.domain.Page<RefreshTokenEntity> springPage,
            PageRequest pageRequest) {

        List<RefreshToken> content = springPage.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return Page.of(content, pageRequest, springPage.getTotalElements());
    }

    /**
     * Convierte List a Page (cuando JPA retorna List en lugar de Page)
     */
    private Page<RefreshToken> toDomainPageFromList(
            List<RefreshTokenEntity> entities,
            PageRequest pageRequest) {

        List<RefreshToken> allTokens = entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        int start = pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getPageSize(), allTokens.size());

        List<RefreshToken> pageContent = start < allTokens.size()
                ? allTokens.subList(start, end)
                : List.of();

        return Page.of(pageContent, pageRequest, allTokens.size());
    }

}
