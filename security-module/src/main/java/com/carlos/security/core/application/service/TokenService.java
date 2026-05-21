package com.carlos.security.core.application.service;

import com.carlos.security.core.application.dto.response.AuthResponse;
import com.carlos.security.core.config.JwtConfig;
import com.carlos.security.core.domain.exception.TokenNotFoundException;
import com.carlos.security.core.domain.model.RefreshToken;
import com.carlos.security.core.domain.model.User;
import com.carlos.security.core.domain.repository.RefreshTokenRepository;
import com.carlos.security.core.infrastructure.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;

    /**
     * Genera un par de tokens (access + refresh) para el usuario autenticado.
     * Si el usuario ya tiene un token activo, lo revoca antes de crear uno nuevo.
     * Guarda el refresh token en la base de datos junto con el dispositivo del cliente.
     */
    public AuthResponse generateTokenPair(User user, String ipAddress, String userAgent) {

        // Extraer roles activos del usuario para incluirlos en el access token
        List<String> roles = new ArrayList<>(user.getRoleNames());

        // El subject del JWT debe ser el email porque UserDetailsServiceImpl busca por email
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail().getValue(), roles);

        // Generar el refresh token como UUID aleatorio (no es JWT, se guarda en BD)
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken();

        // Si el usuario ya tiene un refresh token activo, revocarlo antes de crear uno nuevo
        // Esto evita que el mismo usuario acumule múltiples tokens activos
        refreshTokenRepository.findActiveByUserId(user.getId())
                .ifPresent(existing -> refreshTokenRepository.save(existing.revoke()));

        // Construir el objeto de dominio RefreshToken con todos sus metadatos
        // expiryDate: fecha exacta de expiración calculada desde la config (ms → segundos)
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .userId(user.getId())
                .expiryDate(LocalDateTime.now().plusSeconds(
                        jwtConfig.getRefreshTokenExpiration() / 1000))
                .revoked(false)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(refreshToken);

        // Construir la respuesta con ambos tokens
        // expiresIn sigue la convención OAuth2: segundos restantes (no fecha absoluta)
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .expiresIn(jwtConfig.getAccessTokenExpiration() / 1000)
                .username(user.getUsername())
                .roles(roles)
                .build();
    }

    /**
     * Revoca un refresh token específico.
     * Caso de uso: logout de un solo dispositivo.
     */
    public void revokeToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new TokenNotFoundException());

        refreshTokenRepository.save(token.revoke());
    }

    /**
     * Revoca todos los refresh tokens de un usuario.
     * Caso de uso: "Cerrar sesión en todos los dispositivos".
     */
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }
}
