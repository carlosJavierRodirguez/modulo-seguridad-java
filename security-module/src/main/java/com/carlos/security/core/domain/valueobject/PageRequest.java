package com.carlos.security.core.domain.valueobject;

import lombok.Builder;
import lombok.Getter;
import lombok.EqualsAndHashCode;
import org.hibernate.query.SortDirection;

/**
 * Representa una solicitud de paginación
 * <p>
 * DOMINIO: No depende de Spring (org.springframework.data.domain.Pageable)
 *
 * @param pageNumber    Número de página (empieza en 0)
 * @param pageSize      Cantidad de elementos por página
 * @param sortBy        Campo por el cual ordenar (opcional)
 * @param sortDirection Dirección de ordenamiento (ASC/DESC)
 */

@Getter
@Builder
@EqualsAndHashCode
public class PageRequest {

    private final int pageNumber;
    private final int pageSize;
    private final String sortBy;
    private final SortDirection sortDirection;

    // Valores por defecto
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Constructor con validaciones
     */
    private PageRequest(int pageNumber, int pageSize, String sortBy, SortDirection sortDirection) {
        validatePageNumber(pageNumber);
        validatePageSize(pageSize);

        this.pageNumber = pageNumber;
        this.pageSize = Math.min(pageSize, MAX_PAGE_SIZE); // Protección contra valores excesivos
        this.sortBy = sortBy;
        this.sortDirection = sortDirection != null ? sortDirection : SortDirection.ASC;
    }

    /**
     * Crea una petición de página por defecto
     */
    public static PageRequest of(int pageNumber, int pageSize) {
        return PageRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortDirection(SortDirection.ASC)
                .build();
    }

    /**
     * Crea una petición de página con ordenamiento
     */
    public static PageRequest of(int pageNumber, int pageSize, String sortBy, SortDirection sortDirection) {
        return PageRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
    }

    /**
     * Crea la primera página con tamaño por defecto
     */
    public static PageRequest defaultPage() {
        return PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
    }

    /**
     * Calcula el offset (desplazamiento) para consultas SQL
     * Ejemplo: Página 2, tamaño 20 → offset 20
     */
    public int getOffset() {
        return pageNumber * pageSize;
    }

    /**
     * Obtiene la siguiente página
     */
    public PageRequest next() {
        return PageRequest.builder()
                .pageNumber(this.pageNumber + 1)
                .pageSize(this.pageSize)
                .sortBy(this.sortBy)
                .sortDirection(this.sortDirection)
                .build();
    }

    /**
     * Obtiene la página anterior (si existe)
     */
    public PageRequest previous() {
        int prevPage = Math.max(0, this.pageNumber - 1);
        return PageRequest.builder()
                .pageNumber(prevPage)
                .pageSize(this.pageSize)
                .sortBy(this.sortBy)
                .sortDirection(this.sortDirection)
                .build();
    }

    /**
     * Verifica si es la primera página
     */
    public boolean isFirst() {
        return pageNumber == 0;
    }

    // Validaciones
    private void validatePageNumber(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
    }

    private void validatePageSize(int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }
        if (pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    String.format("Page size cannot exceed %d", MAX_PAGE_SIZE)
            );
        }
    }

    /**
     * Enum para dirección de ordenamiento
     */
    public enum SortDirection {
        ASC,
        DESC
    }

}
