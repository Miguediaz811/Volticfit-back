package com.proyecto.volticfit.dto;

import lombok.Data;

/**
 * DTO para enviar mensajes de respuesta.
 * Se han creado los constructores manualmente por requerimiento técnico.
 */
@Data
public class MessageResponseDTO {
    
    private String message;

    /**
     * Constructor vacío requerido por frameworks como Jackson.
     */
    public MessageResponseDTO() {
    }

    /**
     * Constructor con parámetros para inicializar el mensaje.
     * * @param message El texto de la respuesta.
     */
    public MessageResponseDTO(String message) {
        this.message = message;
    }
}