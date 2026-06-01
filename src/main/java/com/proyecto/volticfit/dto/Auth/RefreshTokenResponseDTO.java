package com.proyecto.volticfit.dto.Auth;

import lombok.Data;
/**
 * DTO para refrescar el token de autenticación, incluyendo el nuevo token JWT generado.
 */
@Data
public class RefreshTokenResponseDTO {
    private String jwt;
}
