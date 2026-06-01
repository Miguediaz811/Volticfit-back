package com.proyecto.volticfit.dto.Exercise;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para completar un ejercicio, incluyendo el ID de la rutina y el ID de la máquina asociada al ejercicio.
 */
@Data
public class CompleteExerciseDTO {
    
    @NotNull(message = "Routine ID is required")
    private Long routineId;
 
    @NotNull(message = "Machine ID is required")
    private Long machineId;
}
