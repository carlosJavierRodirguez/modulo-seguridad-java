package com.carlos.security.core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "roles",
        indexes = {
                @Index(name = "idx_role_name", columnList = "name")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RoleEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    /**
     * Relación Many-to-Many con PermissionEntity
     * <p>
     * - Un rol tiene MUCHOS permisos
     * - Un permiso puede estar en MUCHOS roles
     * <p>
     * FETCH: EAGER vs LAZY
     * - EAGER: Carga los permisos inmediatamente al cargar el rol
     * - LAZY: Carga los permisos solo cuando los accedes
     * <p>
     * Uso EAGER porque Spring Security necesita los permisos al autenticar
     * <p>
     * CASCADE:
     * - MERGE: Al actualizar el rol, actualiza la relación
     * - NO usamos REMOVE: No queremos eliminar permisos al eliminar un rol
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    @Builder.Default
    private Set<PermissionEntity> permissions = new HashSet<>();


    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Callback antes de persistir
     */
    @PrePersist
    protected void onCreate() {
        if (active == null) active = true;
    }

}
