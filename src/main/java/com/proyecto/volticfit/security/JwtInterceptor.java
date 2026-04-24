package com.proyecto.volticfit.security;

import com.proyecto.volticfit.service.JwtService;
import com.proyecto.volticfit.service.TokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenBlackListService tokenBlackListService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Permitir todas las rutas que empiecen por /auth/ (Login, Registro, Recuperar)
        if (request.getRequestURI().startsWith("/auth/")) {
            return true;
        }

        // 2. Revisar el Header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = authHeader.substring(7);

        // 3. Validar token y que no esté en la lista negra
        if (jwtService.isTokenValid(token) && !tokenBlackListService.isBlacklisted(token)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}