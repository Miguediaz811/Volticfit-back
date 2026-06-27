package com.proyecto.volticfit.dto.Sanctions;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear una sancion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSanctionDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotBlank(message = "La descripcion de la sancion es obligatoria")
    @Size(max = 250, message = "La descripcion de la sancion no puede superar 250 caracteres")
    private String description;

    @NotBlank(message = "El tipo de sancion es obligatorio")
    private String type;

    /**
     * Clasificacion de la sancion. Valores validos: leve, moderada, grave.
     */
    @NotBlank(message = "La clasificacion de la sancion es obligatoria")
    @Pattern(regexp = "leve|moderada|grave", message = "La clasificacion debe ser: leve, moderada o grave")
    private String clasificacion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de finalizacion es requerida")
    private LocalDate endDate;
}
