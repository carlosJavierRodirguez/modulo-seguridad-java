package com.carlos.security.core.config;

import com.carlos.security.core.infrastructure.security.UserDetailsServiceImpl;
import com.carlos.security.core.infrastructure.security.jwt.JwtAuthenticationEntryPoint;
import com.carlos.security.core.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.carlos.security.core.shared.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity        // activa Spring Security en toda la aplicación
@EnableMethodSecurity     // habilita @PreAuthorize en los controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PasswordEncoder passwordEncoder; // se inyecta el bean de PasswordEncoderConfig, no la clase

    // Define las reglas de seguridad HTTP: qué rutas proteger, filtros, sesiones y errores
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Las APIs REST con JWT no usan formularios ni cookies de sesión,
                // por lo que CSRF no aplica
                .csrf(csrf -> csrf.disable())

                // JWT es stateless: cada request se autentica con su propio token,
                // Spring no debe crear ni usar sesiones HTTP
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define qué rutas son públicas y cuáles requieren token
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityConstants.PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(SecurityConstants.SWAGGER_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )

                // Cuando una request llega sin token o con token inválido,
                // JwtAuthenticationEntryPoint responde con JSON 401 en lugar de HTML
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // Registra el DaoAuthenticationProvider que conecta UserDetailsService + PasswordEncoder
                .authenticationProvider(authenticationProvider())

                // Inserta el filtro JWT ANTES del filtro estándar de usuario/contraseña
                // Así el usuario queda autenticado antes de que Spring Security lo verifique
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Conecta UserDetailsServiceImpl con PasswordEncoder para el proceso de autenticación:
    // cuando alguien hace login, Spring usa este provider para verificar usuario y contraseña
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Spring Security 6.4+: UserDetailsService va directo en el constructor
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsServiceImpl);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // Expone el AuthenticationManager como bean para que AuthenticationService
    // pueda ejecutar el proceso de login programáticamente
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
