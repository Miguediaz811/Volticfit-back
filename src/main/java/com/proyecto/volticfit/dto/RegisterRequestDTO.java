package com.proyecto.volticfit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "El campo es obligatorio")
    private String nombres;

    @NotBlank(message = "El campo es obligatorio")
    private String apellidos;

    @NotBlank(message = "El campo es obligatorio")
    @Email(message = "El correo no es válido")
    private String correo;

    @NotBlank(message = "El campo es obligatorio")
    private Long telefono;

    @NotBlank(message = "El campo es obligatorio")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    private String rol = "aprendiz";

    private Boolean estado = true;
}