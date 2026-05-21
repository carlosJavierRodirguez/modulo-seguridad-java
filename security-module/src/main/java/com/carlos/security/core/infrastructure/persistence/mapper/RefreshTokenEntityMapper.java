package com.carlos.security.core.infrastructure.persistence.mapper;

import com.carlos.security.core.domain.model.RefreshToken;
import com.carlos.security.core.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre RefreshToken (dominio) y RefreshTokenEntity (JPA)
 * <p>
 * RESPONSABILIDAD:
 * - toEntity(): Dominio → JPA (para guardar en BD)
 * - toDomain(): JPA → Dominio (para usar en lógica de negocio)
 */
@Component
public class RefreshTokenEntityMapper {

    /**
     * Convierte modelo de DOMINIO a entidad JPA
     * <p>
     * Usado cuando: Queremos GUARDAR en base de datos
     *
     * @param domain Modelo de dominio
     * @return Entidad JPA lista para persistir
     */
    public RefreshTokenEntity toEntity(RefreshToken domain) {

        if (domain == null) {
            return null;
        }

        return RefreshTokenEntity.builder()
                .id(domain.getId())
                .token(domain.getToken())
                .userId(domain.getUserId())
                .expiryDate(domain.getExpiryDate())
                .createdAt(domain.getCreatedAt())
                .revoked(domain.isRevoked())
                .revokedAt(domain.getRevokedAt())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .build();
    }

    /**
     * Convierte entidad JPA a modelo de DOMINIO
     * <p>
     * Usado cuando: Recuperamos de base de datos
     *
     * @param entity Entidad JPA
     * @return Modelo de dominio con lógica de negocio
     */
    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) {
            return null;
        }
        return RefreshToken.builder()
                .id(entity.getId())
                .token(entity.getToken())
                .userId(entity.getUserId())
                .expiryDate(entity.getExpiryDate())
                .createdAt(entity.getCreatedAt())
                .revoked(entity.getRevoked())
                .revokedAt(entity.getRevokedAt())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .build();
    }

}