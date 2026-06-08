package com.proyecto.volticfit.dto.Failures;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFailureReportDTO {
    @NotNull(message = "El equipo es obligatorio")
    private Long machineId;

    @NotBlank(message = "La descripcion de la falla es obligatoria")
    private String description;

    private String priority;
}
