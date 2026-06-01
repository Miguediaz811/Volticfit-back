package com.proyecto.volticfit.dto.Auth;

import lombok.Data;
/**
 * DTO para la solicitud de inicio de sesión, incluyendo el correo electrónico y la contraseña del usuario.
 */
@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}