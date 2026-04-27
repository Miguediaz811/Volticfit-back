package com.proyecto.volticfit.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.proyecto.volticfit.entity.Users;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio encargado de la gestión de tokens JWT para Volticfit.
 * Maneja el ciclo de vida de los tokens: creación, validación, refresco y recuperación.
 */
@Service
@Log4j2
public class JwtService {

    private final String secretKey;
    private final Long tokenExpiration;
    private final Long recoveryExpiration;

    /**
     * Constructor con inyección de dependencias desde .yaml.
     * @param secretKey Llave secreta en Base64.
     * @param tokenExpiration Tiempo de expiración de acceso.
     * @param recoveryExpiration Tiempo de expiración para recuperación (Requisito: no quemado).
     */
    public JwtService(
            @Value("${security.jwt.secret-key}") String secretKey,
            @Value("${security.jwt.token-expiration:3600000}") Long tokenExpiration,
            @Value("${security.jwt.recovery-expiration:900000}") Long recoveryExpiration) {
        this.secretKey = secretKey;
        this.tokenExpiration = tokenExpiration;
        this.recoveryExpiration = recoveryExpiration;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token de acceso estándar.
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .claims(Map.of("role", role))
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Genera un token para el flujo de reset de contraseña usando el email.
     */
    public String generateResetToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + recoveryExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Genera un token específico para la recuperación de contraseña usando la entidad Users.
     * Requerido por AuthService:[139,34].
     * @param user Entidad del usuario.
     * @return String JWT.
     */
    public String generateRecoveryToken(Users user) {
        return generateResetToken(user.getEmail());
    }

    /**
     * Refresca un token existente extrayendo sus claims actuales.
     */
    public String refreshToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }
        return generateToken(claims.getSubject(), claims.get("role", String.class));
    }

    /**
     * Extrae el rol almacenado en los claims del token.
     */
    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
    }

    public String extractEmail(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.error("Token inválido: {}", e.getMessage());
            return false;
        }
    /**
     * Extrae el rol para poder refrescar el token o validar permisos
     */
    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
    }
}