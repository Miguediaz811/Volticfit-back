package com.proyecto.volticfit.config;

/**
 * Excepción lanzada cuando se detecta una petición duplicada que aún está
 * siendo procesada (estado PROCESSING). Produce un HTTP 409 Conflict.
 */
public class IdempotencyConflictException extends RuntimeException {

    public IdempotencyConflictException(String message) {
        super(message);
    }
}
