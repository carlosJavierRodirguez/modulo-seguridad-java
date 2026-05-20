package com.carlos.security.core.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class UserResponse {

    UUID id;
    String username;
    String email;
    String firstName;
    String lastName;
    Set<String> roles;
    Boolean enabled;
    LocalDateTime createdAt;
}
