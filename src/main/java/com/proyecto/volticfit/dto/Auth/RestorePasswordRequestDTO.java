package com.proyecto.volticfit.dto.Auth;

import lombok.Data;

/**
 * DTO para restaurar la contraseña de un usuario, incluyendo el correo electrónico, el código de recuperación y la nueva contraseña.
 */
@Data
public class RestorePasswordRequestDTO {
    private String email;
    private String code;
    private String newPassword;
}