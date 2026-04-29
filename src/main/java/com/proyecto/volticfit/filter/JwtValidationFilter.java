package com.proyecto.volticfit.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.UsersRepository;
import com.proyecto.volticfit.service.JwtService;
import com.proyecto.volticfit.service.TokenBlackListService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filtro de seguridad que intercepta las peticiones HTTP para validar el token JWT.
 * Verifica la autenticidad del token, comprueba la lista negra y establece los atributos del usuario en la petición.
 */
@Component
@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlackListService blacklistService;
    private final UsersRepository usersRepository;

    /**
     * Procesa cada petición entrante para validar el esquema Bearer y la integridad del token.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Header Authorization faltante\"}");
            return;
        }

        String token = authHeader.substring(7);

        if (blacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token revocado (sesión cerrada)\"}");
            return;
        }

        try {
            if (jwtService.isTokenValid(token)) {
                String email = jwtService.extractEmail(token);
                String role = jwtService.extractRole(token);
                
                Users user = usersRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para el token"));

                request.setAttribute("userId", user.getIdUser());
                request.setAttribute("email", email);
                request.setAttribute("role", role);

                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token inválido o expirado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Error al validar el token\"}");
        }
    }

    /**
     * Define las rutas que quedan excluidas de la validación de JWT.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        
        /** Solo añadimos las rutas públicas y de recuperación para que no soliciten token */
        return path.equals("/auth/login")
                || path.equals("/auth/register")
                || path.equals("/auth/refresh")
                || path.equals("/auth/forgot-password")
                || path.equals("/auth/recovery/verify")
                || path.equals("/auth/recovery/reset")
                || path.equals("/auth/restore-password");
    }
}