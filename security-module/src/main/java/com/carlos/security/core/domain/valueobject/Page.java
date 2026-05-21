package com.carlos.security.core.domain.valueobject;

import lombok.Builder;
import lombok.Getter;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Representa una página de resultados
 * <p>
 * DOMINIO: Independiente de Spring (org.springframework.data.domain.Page)
 *
 * @param <T> Tipo de elementos en la página
 */
@Getter
@Builder
@EqualsAndHashCode
public class Page<T> {

    /**
     * Contenido de la página actual
     */
    private final List<T> content;

    /**
     * Número de página actual (empieza en 0)
     */
    private final int pageNumber;

    /**
     * Cantidad de elementos por página
     */
    private final int pageSize;

    /**
     * Total de elementos en TODAS las páginas
     */
    private final long totalElements;

    /**
     * Total de páginas disponibles
     */
    private final int totalPages;

    /**
     * ¿Es la primera página?
     */
    private final boolean first;

    /**
     * ¿Es la última página?
     */
    private final boolean last;

    /**
     * ¿Tiene página siguiente?
     */
    private final boolean hasNext;

    /**
     * ¿Tiene página anterior?
     */
    private final boolean hasPrevious;

    /**
     * Número de elementos en esta página
     */
    private final int numberOfElements;

    /**
     * ¿La página está vacía?
     */
    private final boolean empty;

    /**
     * Constructor privado con cálculos automáticos
     */
    private Page(List<T> content, int pageNumber, int pageSize, long totalElements,
                 int totalPages, boolean first, boolean last, boolean hasNext,
                 boolean hasPrevious, int numberOfElements, boolean empty) {
        this.content = content != null ? content : List.of();
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.numberOfElements = numberOfElements;
        this.empty = empty;
    }

    /**
     * Crea una página con cálculos automáticos
     */
    public static <T> Page<T> of(List<T> content, PageRequest pageRequest, long totalElements) {
        int totalPages = calculateTotalPages(pageRequest.getPageSize(), totalElements);
        int pageNumber = pageRequest.getPageNumber();

        return Page.<T>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageRequest.getPageSize())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(pageNumber == 0)
                .last(pageNumber >= totalPages - 1)
                .hasNext(pageNumber < totalPages - 1)
                .hasPrevious(pageNumber > 0)
                .numberOfElements(content != null ? content.size() : 0)
                .empty(content == null || content.isEmpty())
                .build();
    }

    /**
     * Crea una página vacía
     */
    public static <T> Page<T> empty(PageRequest pageRequest) {
        return Page.of(List.of(), pageRequest, 0);
    }

    /**
     * Transforma el contenido de la página a otro tipo
     * <p>
     * Útil para convertir Page<UserEntity> → Page<User>
     *
     * @param mapper Función de transformación
     * @return Nueva página con contenido transformado
     */
    public <U> Page<U> map(Function<T, U> mapper) {
        List<U> transformedContent = this.content.stream()
                .map(mapper)
                .collect(Collectors.toList());

        return Page.<U>builder()
                .content(transformedContent)
                .pageNumber(this.pageNumber)
                .pageSize(this.pageSize)
                .totalElements(this.totalElements)
                .totalPages(this.totalPages)
                .first(this.first)
                .last(this.last)
                .hasNext(this.hasNext)
                .hasPrevious(this.hasPrevious)
                .numberOfElements(transformedContent.size())
                .empty(transformedContent.isEmpty())
                .build();
    }

    /**
     * Calcula el total de páginas
     */
    private static int calculateTotalPages(int pageSize, long totalElements) {
        if (pageSize == 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / (double) pageSize);
    }

}
