package com.carlos.security.core.presentation.controller;

import com.carlos.security.core.application.dto.request.LoginRequest;
import com.carlos.security.core.application.dto.request.RefreshTokenRequest;
import com.carlos.security.core.application.dto.request.RegisterRequest;
import com.carlos.security.core.application.dto.response.AuthResponse;
import com.carlos.security.core.application.service.AuthenticationService;
import com.carlos.security.core.shared.constants.SecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SecurityConstants.AUTH_BASE_PATH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse response = authenticationService.login(request, ipAddress, userAgent);

        return ResponseEntity.ok(response);
    }

    // POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse response = authenticationService.register(request, ipAddress, userAgent);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /api/v1/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse response = authenticationService.refreshToken(request, ipAddress, userAgent);

        return ResponseEntity.ok(response);
    }
}
