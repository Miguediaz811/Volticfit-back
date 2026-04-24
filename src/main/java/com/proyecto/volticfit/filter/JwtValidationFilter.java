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

@Component
@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlackListService blacklistService;
    private final UsersRepository usersRepository;

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
                String correo = jwtService.extractCorreo(token);
                String rol = jwtService.extractRol(token);
                
                Users user = usersRepository.findByCorreo(correo)
                        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para el token"));

                request.setAttribute("userId", user.getId_usuario());
                request.setAttribute("correo", correo);
                request.setAttribute("rol", rol);

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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Solo añadimos la ruta de restore para que no te pida token
        return path.equals("/auth/login")
                || path.equals("/auth/register")
                || path.equals("/auth/refresh")
                || path.equals("/auth/restore-password");
    }
}