package com.proyecto.volticfit.dto;

/**
 * DTO que representa el resultado de la validación de un QR.
 */
public class QrValidationResponseDTO {

    private String message;
    private boolean valid;

    /**
     * Constructor del DTO.
     *
     * @param message mensaje descriptivo del resultado
     * @param valid indica si el QR es válido o no
     */
    public QrValidationResponseDTO(String message, boolean valid) {
        this.message = message;
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public boolean isValid() {
        return valid;
    }
}
