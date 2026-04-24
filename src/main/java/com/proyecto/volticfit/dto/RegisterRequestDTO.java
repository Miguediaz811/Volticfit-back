package com.proyecto.volticfit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@Data
@RequiredArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "El campo es obligatorio")
    @NotNull(message = "El campo no puede ser nulo")
    private String names;
  
    @NotBlank(message = "El campo es obligatorio")
    @NotNull(message = "El campo no puede ser nulo")
    private String surnames;

    @NotBlank(message = "El campo es obligatorio")
    @NotNull(message = "El campo no puede ser nulo")
    private String docType;

    @NotBlank(message = "El campo es obligatorio")
    @NotNull(message = "El campo no puede ser nulo")
    private String docNum;

    @NotBlank(message = "El campo es obligatorio")
    @NotNull(message = "El campo no puede ser nulo")
    @Email(message = "El correo no es válido")
    private String email;

    @NotBlank(message = "El campo es obligatorio")
    @NotNull(message = "El campo no puede ser nulo")
    private Long phone;

    @NotBlank(message = "El campo es obligatorio")
    @NotNull(message = "El campo no puede ser nulo")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private String role = "aprendiz";

    private Boolean state = true;
}