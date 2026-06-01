package com.proyecto.volticfit.dto.Auth;

import lombok.Data;
/**
 * DTO para solicitar el restablecimiento de contraseña, incluyendo el correo electrónico del usuario.
 */
@Data
public class ForgotPasswordRequestDTO {
    private String email;
}