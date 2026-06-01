package com.proyecto.volticfit.dto.Auth;

import com.proyecto.volticfit.dto.MessageResponseDTO;

import lombok.Data;
/**
 * DTO para responder con la información de inicio de sesión, incluyendo el token JWT.
 */
@Data
public class LoginResponseDTO extends MessageResponseDTO {
    private String jwt;
}