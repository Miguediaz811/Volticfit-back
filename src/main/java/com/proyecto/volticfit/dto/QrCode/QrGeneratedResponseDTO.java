package com.proyecto.volticfit.dto.QrCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para responder con el código QR generado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrGeneratedResponseDTO {

    private String qrBase64;
    
    private String token;
}
