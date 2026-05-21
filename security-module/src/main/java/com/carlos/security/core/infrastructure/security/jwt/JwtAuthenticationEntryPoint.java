package com.carlos.security.core.infrastructure.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // respuesta HTTP
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //codigo 401

        String jsonPayload = String.format(
                "{\"status\": %d, \"error\": \"No autorizado\", \"message\": \"%s\"}",
                HttpServletResponse.SC_UNAUTHORIZED,
                authException.getMessage()
        );

        response.getWriter().write(jsonPayload);
    }
}
