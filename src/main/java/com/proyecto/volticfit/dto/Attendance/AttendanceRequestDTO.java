package com.proyecto.volticfit.dto.Attendance;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttendanceRequestDTO {
 
    @NotBlank(message = "Token is required")
    private String token;
}
