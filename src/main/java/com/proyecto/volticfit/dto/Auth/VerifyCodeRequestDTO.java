package com.proyecto.volticfit.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para la verificación del código de recuperación
 */
@Data
public class VerifyCodeRequestDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String code;
}