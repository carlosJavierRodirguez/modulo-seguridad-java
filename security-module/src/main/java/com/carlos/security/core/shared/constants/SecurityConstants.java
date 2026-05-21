package com.carlos.security.core.shared.constants;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    // JWT
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTH_HEADER = "Authorization";
    public static final long ACCESS_TOKEN_EXPIRATION_MS = 15 * 60 * 1000L;        // 15 minutos
    public static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L; // 7 días

    // API paths
    public static final String AUTH_BASE_PATH = "/api/v1/auth";
    public static final String USERS_BASE_PATH = "/api/v1/users";
    public static final String ROLES_BASE_PATH = "/api/v1/roles";

    // Public endpoints (no requieren token)
    public static final String[] PUBLIC_ENDPOINTS = {
            AUTH_BASE_PATH + "/login",
            AUTH_BASE_PATH + "/register",
            AUTH_BASE_PATH + "/refresh",
    };

    // Seguridad de cuenta
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    public static final long ACCOUNT_LOCK_DURATION_MINUTES = 30L;

    // Password policy
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";

    // Page Request
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

}
