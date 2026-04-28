package com.proyecto.volticfit.dto.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class RestorePasswordRequestDTO {

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Debe ser un formato de correo válido")
    private String email;

    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    private String newPassword;
}