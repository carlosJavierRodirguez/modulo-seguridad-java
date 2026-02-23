package com.carlos.security.core.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public abstract class Auditable {

    protected final LocalDateTime createdAt;
    protected final LocalDateTime updatedAt;
    protected final String createdBy;
    protected final String updatedBy;

    public boolean isCreatedToday() {
        return createdAt != null && createdAt.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    public boolean isModified() {
        return updatedAt != null && !updatedAt.equals(createdAt);
    }

}
