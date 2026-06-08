package com.proyecto.volticfit.dto.Sanctions;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear una sanción
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSanctionDTO {
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotBlank(message = "La descripción de la sanción es obligatoria")
    private String description;

    @NotBlank(message = "El tipo de sanción es obligatorio")
    private String type;

    /**
     * Clasificación de la sanción. Valores válidos: leve, moderada, grave.
     */
    @NotBlank(message = "La clasificación de la sanción es obligatoria")
    @Pattern(regexp = "leve|moderada|grave", message = "La clasificación debe ser: leve, moderada o grave")
    private String clasificacion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de finalización es requerida")
    private LocalDate endDate;
}
