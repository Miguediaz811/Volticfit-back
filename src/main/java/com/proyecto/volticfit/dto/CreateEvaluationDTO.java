package com.proyecto.volticfit.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para crear una evaluación
 */
@Data
public class CreateEvaluationDTO {
    
    @NotNull(message = "La fecha es requerida")
    private LocalDate date;

    @NotNull(message = "El tiempo de inicio es requerido")
    private LocalTime startTime;

    @NotNull(message = "El ID del instructor es requerido")
    private Long instructorId;

    @Size(max = 100, message = "Las notas no pueden superar 100 caracteres")
    private String notes;

}
