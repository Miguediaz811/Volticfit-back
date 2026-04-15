package com.proyecto.volticfit.service;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio encargado de la gestión de JSON Web Tokens (JWT).
 * Proporciona métodos para crear, validar, leer y refrescar tokens de acceso.
 */
@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token-expiration}")
    private Long tokenExpiration;

    /**
     * Genera un nuevo token JWT para un usuario con su correo y rol.
     */
    public String generateToken(String correo, String rol) {
        return Jwts.builder()
                .claims(Map.of("rol", rol))
                .subject(correo)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Transforma la clave secreta (Base64) a un objeto SecretKey utilizable por la librería.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Verifica si un token es íntegro y no ha expirado.
     */
    public Boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    public String extractCorreo(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public String extractRol(String token) {
        return extractClaims(token, c -> c.get("rol", String.class));
    }

    /**
     * Refresca el token, incluso si está expirado (permite renovar sesión sin re-login).
     * @param token JWT viejo (válido o expirado)
     * @return Nuevo JWT firmado
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
            // Si expiró, recuperamos los datos del cuerpo del error
            claims = e.getClaims();
        } catch (JwtException e) {
            throw new RuntimeException("Token inválido: " + e.getMessage());
        }

        return generateToken(claims.getSubject(), claims.get("rol", String.class));
    }
}
