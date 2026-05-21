package com.carlos.security.core.application.service;

import com.carlos.security.core.application.dto.request.LoginRequest;
import com.carlos.security.core.application.dto.request.RefreshTokenRequest;
import com.carlos.security.core.application.dto.request.RegisterRequest;
import com.carlos.security.core.application.dto.response.AuthResponse;
import com.carlos.security.core.domain.exception.InvalidCredentialsException;
import com.carlos.security.core.domain.exception.TokenExpiredException;
import com.carlos.security.core.domain.exception.TokenNotFoundException;
import com.carlos.security.core.domain.exception.UserAlreadyExistsException;
import com.carlos.security.core.domain.exception.UserNotFoundException;
import com.carlos.security.core.domain.model.RefreshToken;
import com.carlos.security.core.domain.model.User;
import com.carlos.security.core.domain.repository.RefreshTokenRepository;
import com.carlos.security.core.domain.repository.UserRepository;
import com.carlos.security.core.domain.valueobject.Email;
import com.carlos.security.core.domain.valueobject.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Autentica un usuario con email y contraseña.
     * Si las credenciales son correctas genera y devuelve un par de tokens.
     * ipAddress y userAgent se guardan en el refresh token para rastreo de dispositivos.
     */
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {

        // Spring Security verifica email + contraseña contra la BD usando UserDetailsServiceImpl
        // Si las credenciales son incorrectas lanza BadCredentialsException
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            // Convertimos la excepción de Spring a nuestra excepción de dominio
            throw new InvalidCredentialsException(request.getEmail());
        }

        // Las credenciales son válidas — cargamos el objeto User del dominio
        // (Spring Security solo devuelve UserDetails, no nuestro dominio completo)
        User user = userRepository.findByEmail(Email.of(request.getEmail())).orElseThrow(() -> new UserNotFoundException("email", request.getEmail()));

        // Generamos el par access token + refresh token y lo devolvemos
        return tokenService.generateTokenPair(user, ipAddress, userAgent);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Verifica que el email y username no estén en uso antes de crear la cuenta.
     * Al registrar se genera automáticamente un par de tokens para no requerir un login adicional.
     */
    public AuthResponse register(RegisterRequest request, String ipAddress, String userAgent) {

        // Verificar que el email no esté registrado
        if (userRepository.existsByEmail(Email.of(request.getEmail()))) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }

        // Verificar que el telefono celular no esté en uso
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAlreadyExistsException("phoneNumber", request.getPhoneNumber());
        }

        // Hashear la contraseña con BCrypt antes de guardarla — nunca se guarda en texto plano
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Construir el objeto de dominio User con todos sus campos iniciales
        // Password.ofEncoded() indica que ya está hasheada — no se vuelve a validar el formato
        User newUser = User.builder()
                .username(request.getUsername())
                .email(Email.of(request.getEmail()))
                .password(Password.ofEncoded(encodedPassword))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .build();

        // Persistir el usuario en la base de datos
        User savedUser = userRepository.save(newUser);

        // Generar tokens para la sesión inmediata — el usuario no necesita hacer login después del registro
        return tokenService.generateTokenPair(savedUser, ipAddress, userAgent);
    }

    /**
     * Renueva el access token usando un refresh token válido.
     * Caso de uso: el access token expiró (15 min) pero el refresh token sigue vigente (7 días).
     * Al renovar se invalida el refresh token anterior y se emite uno nuevo.
     */
    public AuthResponse refreshToken(RefreshTokenRequest request, String ipAddress, String userAgent) {

        // Buscar el refresh token en la base de datos
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken()).orElseThrow(TokenNotFoundException::new);

        // Verificar que el token no esté revocado ni expirado
        // isValid() del dominio comprueba ambas condiciones
        if (!refreshToken.isValid()) {
            throw new TokenExpiredException(refreshToken.getExpiryDate());
        }

        // Cargar el usuario dueño del token
        User user = userRepository.findById(refreshToken.getUserId()).orElseThrow(() -> new UserNotFoundException(refreshToken.getUserId()));

        // generateTokenPair revoca automáticamente el token anterior y emite uno nuevo
        return tokenService.generateTokenPair(user, ipAddress, userAgent);
    }
}
