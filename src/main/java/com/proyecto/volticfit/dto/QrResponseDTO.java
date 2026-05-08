package com.proyecto.volticfit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO containing the generated QR code image as base64.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrResponseDTO {
    private String qrBase64;
    private String token;
}