package com.carlos.security.core.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AuthResponse {

    String accessToken;
    String refreshToken;

    @Builder.Default
    String tokenType = "Bearer";

    Long expiresIn;
    String username;
    List<String> roles;
}
