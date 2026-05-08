package com.proyecto.volticfit.dto.QrCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrResponseDTO {

    private String qrBase64;
    
    private String token;
}
