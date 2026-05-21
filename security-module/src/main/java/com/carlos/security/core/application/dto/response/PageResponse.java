package com.carlos.security.core.application.dto.response;

import com.carlos.security.core.domain.valueobject.Page;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean first;
    private boolean last;

    public static <T> PageResponse<T> from(Page<T> domainPage) {
        return PageResponse.<T>builder()
                .content(domainPage.getContent())
                .page(domainPage.getPageNumber())
                .size(domainPage.getPageSize())
                .totalElements(domainPage.getTotalElements())
                .totalPages(domainPage.getTotalPages())
                .hasNext(domainPage.isHasNext())
                .hasPrevious(domainPage.isHasPrevious())
                .first(domainPage.isFirst())
                .last(domainPage.isLast())
                .build();
    }
}
