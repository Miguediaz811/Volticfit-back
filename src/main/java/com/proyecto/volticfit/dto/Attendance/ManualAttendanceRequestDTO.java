package com.proyecto.volticfit.dto.Attendance;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ManualAttendanceRequestDTO {
    
    @NotBlank(message = "Document number is required")
    private String docNumber;
}
