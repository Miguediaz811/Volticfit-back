package com.proyecto.volticfit.dto.Attendance;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para registrar la asistencia de un usuario, incluyendo el token de autenticación del usuario.
 */
@Data
public class AttendanceRequestDTO {
 
    @NotBlank(message = "Token is required")
    private String token;
}
