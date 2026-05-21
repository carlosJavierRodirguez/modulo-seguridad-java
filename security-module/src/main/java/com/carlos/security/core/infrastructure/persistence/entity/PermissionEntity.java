package com.carlos.security.core.infrastructure.persistence.entity;

import com.carlos.security.core.domain.model.Permission;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(
        name = "permissions",
        indexes = {
                @Index(name = "idx_permission_name", columnList = "name"),
                @Index(name = "idx_permission_resource_type", columnList = "resource, type")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_permission_name", columnNames = "name"),
                @UniqueConstraint(name = "uk_resource_type", columnNames = {"resource", "type"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermissionEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Nombre único del permiso
     * Ejemplo: "CREATE_USER", "DELETE_REPORT"
     */
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    /**
     * Descripción legible del permiso
     * Ejemplo: "Permite crear nuevos usuarios"
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * Recurso sobre el cual actúa el permiso
     * Ejemplo: "users", "reports", "products"
     */
    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    /**
     * Tipo de operación del permiso
     *
     * @Enumerated(EnumType.STRING): - Guarda el NOMBRE del enum en la BD: "CREATE", "READ", etc.
     * - Más legible que EnumType.ORDINAL (guarda números 0,1,2...)
     * - Seguro ante reordenamiento del enum
     */
    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Permission.PermissionType type;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        if (active == null) active = true;
    }

}
