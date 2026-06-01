package com.proyecto.volticfit.dto.Attendance;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para registrar la asistencia de un usuario de forma manual, utilizando el número de documento del usuario.
 */
@Data
public class ManualAttendanceRequestDTO {
    
    @NotBlank(message = "Document number is required")
    private String docNumber;
}
