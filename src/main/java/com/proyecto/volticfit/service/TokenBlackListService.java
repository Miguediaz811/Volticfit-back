package com.proyecto.volticfit.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlackListService {

    // Token + fecha de expiración
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    /**
     * Agrega un token a la blacklist con su fecha de expiración
     */
    public void blacklistToken(String token, Instant expirationDate) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("El token no puede ser nulo o vacío");
        }

        blacklist.put(token, expirationDate);
    }

    /**
     * Verifica si el token está bloqueado
     */
    public boolean isBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        cleanExpiredTokens();

        return blacklist.containsKey(token);
    }

    /**
     * Elimina tokens expirados automáticamente
     */
    private void cleanExpiredTokens() {
        Instant now = Instant.now();

        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }

    /**
     * Simula un logout: invalida el token
     */
    public void logout(String token, Instant expirationDate) {
        blacklistToken(token, expirationDate);
    }
}