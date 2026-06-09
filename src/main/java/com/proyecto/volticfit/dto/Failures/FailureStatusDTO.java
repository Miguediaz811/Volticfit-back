package com.proyecto.volticfit.dto.Failures;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FailureStatusDTO {
    @NotBlank(message = "El estado es obligatorio")
    private String status;
}
