package com.proyecto.volticfit.dto.Sanctions;

import java.time.LocalDate;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO para actualizar una sanción
 */
@Data
public class UpdateSanctionDTO {
    
    private String description;

    private String type;

    @Pattern(regexp = "leve|moderada|grave", message = "La clasificación debe ser: leve, moderada o grave")
    private String clasificacion;

    private LocalDate startDate;
    
    private LocalDate endDate;
}
