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

@Service
public class JwtService {

    private final String secretKey;
    
    private final Long tokenExpiration;

    private final Long QRtokenExpiration;

    // El constructor recibe las propiedades del application.yml
    public JwtService(
            @Value("${security.jwt.secret-key}") String secretKey,
            @Value("${security.jwt.token-expiration}") Long tokenExpiration,
            @Value("${QRToken.QRtoken-expiration}") Long QRtokenExpiration
            ) {
        this.secretKey = secretKey;
        this.tokenExpiration = tokenExpiration;
        this.QRtokenExpiration = QRtokenExpiration;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

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
     * Método para recuperar contraseña (fijo a 15 min = 900,000 ms)
     */
    public String generateResetToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(getSigningKey())
                .compact();
    }

    public String refreshToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // Allow refresh even if token is expired
            claims = e.getClaims();
        } catch (JwtException e) {
            throw new RuntimeException("Token is invalid: " + e.getMessage());
        }

        return generateToken(claims.getSubject(), claims.get("role", String.class));

    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
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

    /**
     * Extrae el rol para poder refrescar el token o validar permisos
     */
    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
    }

        /**
     * Genera un token JWT de uso exclusivo para códigos QR.
     *
     * <p>Este token incluye un claim "type" con valor "QR" que permite
     * diferenciarlo de los tokens de autenticación normales.</p>
     *
     * <p>El tiempo de expiración es corto (30 segundos) para evitar
     * reutilización o uso indebido.</p>
     *
     * @param email correo del usuario autenticado
     * @param role rol del usuario (se mantiene consistencia con otros tokens)
     * @return token JWT diseñado para ser usado como QR
     */
    public String generateQrToken(String email, String role) {
        return Jwts.builder()
                .claims(Map.of(
                        "role", role,
                        "type", "QR"
                ))
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() +  QRtokenExpiration)) 
                .signWith(getSigningKey())
                .compact();
    }

        /**
     * Verifica si un token JWT corresponde a un código QR.
     *
     * Se valida mediante el claim "type" que debe ser igual a "QR".
     *
     * @param token token JWT a evaluar
     * @return true si es un token QR, false en caso contrario
     */
    public boolean isQrToken(String token) {
        try {
            String type = extractClaims(token, claims -> claims.get("type", String.class));
            return "QR".equals(type);
        } catch (Exception e) {
            return false;
        }
    }
}