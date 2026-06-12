package com.proyecto.volticfit.enums;

/**
 * Estados posibles de un registro de idempotencia.
 */
public enum IdempotencyStatus {
    /** La petición está siendo procesada actualmente */
    PROCESSING,
    /** La petición fue procesada exitosamente y la respuesta está almacenada */
    COMPLETED,
    /** La petición falló y puede ser reintentada */
    FAILED
}
