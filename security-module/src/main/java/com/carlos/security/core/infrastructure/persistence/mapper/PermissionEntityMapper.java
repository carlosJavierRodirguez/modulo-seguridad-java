package com.carlos.security.core.infrastructure.persistence.mapper;

import com.carlos.security.core.domain.model.Permission;
import com.carlos.security.core.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Permission (dominio) y PermissionEntity (JPA)
 * <p>
 * RESPONSABILIDAD:
 * - toEntity(): Dominio → JPA (para guardar en BD)
 * - toDomain(): JPA → Dominio (para usar en lógica de negocio)
 */
@Component
public class PermissionEntityMapper {

    /**
     * Convierte modelo de DOMINIO a entidad JPA
     * <p>
     * Usado cuando: Queremos GUARDAR en base de datos
     *
     * @param domain Modelo de dominio
     * @return Entidad JPA lista para persistir
     */
    public PermissionEntity toEntity(Permission domain) {
        if (domain == null) {
            return null;
        }

        return PermissionEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .resource(domain.getResource())
                .type(domain.getType())  // ← El enum se copia directamente
                .active(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
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
    public Permission toDomain(PermissionEntity entity) {
        if (entity == null) {
            return null;
        }

        return Permission.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .resource(entity.getResource())
                .type(entity.getType())  // ← El enum se copia directamente
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

}
