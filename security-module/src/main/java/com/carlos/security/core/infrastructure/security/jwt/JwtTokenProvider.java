package com.carlos.security.core.infrastructure.security.jwt;

import com.carlos.security.core.config.JwtConfig;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;
    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;

        byte[] keyBytes = HexFormat.of().parseHex(jwtConfig.getSecret());
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");

        OctetSequenceKey octetKey = new OctetSequenceKey.Builder(secretKeySpec).algorithm(JWSAlgorithm.HS256).build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(octetKey));

        this.encoder = new NimbusJwtEncoder(jwkSource);
        this.decoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).build();
    }

    public String generateAccessToken(String username, List<String> roles) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder().subject(username).issuedAt(now).expiresAt(now.plusMillis(jwtConfig.getAccessTokenExpiration())).claim("roles", roles).build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public boolean validateToken(String token) {
        try {
            decoder.decode(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return decoder.decode(token).getSubject();
    }
}
