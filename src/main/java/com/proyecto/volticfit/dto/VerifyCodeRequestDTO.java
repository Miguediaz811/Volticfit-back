package com.proyecto.volticfit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequestDTO {

    @NotBlank(message = "El campo es obligatorio")
    @Email(message = "El correo no es válido")
    private String correo;

    @NotBlank(message = "El campo es obligatorio")
    private String codigo;
}