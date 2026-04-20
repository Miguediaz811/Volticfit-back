package com.proyecto.volticfit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "El campo es obligatorio")
    private String nombres;

    @NotBlank(message = "El campo es obligatorio")
    private String apellidos;

    @NotBlank(message = "El campo es obligatorio")
    @Email(message = "El correo no es válido")
    private String correo;

    @NotBlank(message = "El campo es obligatorio")
    @Size(min = 10, message = "El telefono debe tener al menos 10 números")
    private String telefono;

    @NotBlank(message = "El campo es obligatorio")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    private String rol = "aprendiz";

    private Boolean estado = true;
}