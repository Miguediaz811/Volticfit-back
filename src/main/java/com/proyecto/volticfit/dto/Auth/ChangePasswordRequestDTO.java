package com.proyecto.volticfit.dto.Auth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para cambiar la contraseña de un usuario, incluyendo la contraseña actual y la nueva contraseña.
 */
@Data
public class ChangePasswordRequestDTO {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String currentPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
    private String newPassword;

}