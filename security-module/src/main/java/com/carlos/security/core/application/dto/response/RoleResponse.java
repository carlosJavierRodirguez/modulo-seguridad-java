package com.carlos.security.core.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class RoleResponse {

    UUID id;
    String name;
    String description;
    Set<String> permissions;
    boolean active;
    int permissionCount;
}
