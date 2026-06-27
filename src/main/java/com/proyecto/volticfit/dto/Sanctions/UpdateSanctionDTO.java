package com.proyecto.volticfit.dto.Sanctions;

import java.time.LocalDate;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para actualizar una sancion.
 */
@Data
public class UpdateSanctionDTO {

    @Size(max = 250, message = "La descripcion de la sancion no puede superar 250 caracteres")
    private String description;

    private String type;

    @Pattern(regexp = "leve|moderada|grave", message = "La clasificacion debe ser: leve, moderada o grave")
    private String clasificacion;

    private LocalDate startDate;

    private LocalDate endDate;
}
