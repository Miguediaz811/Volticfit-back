package com.proyecto.volticfit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompleteExerciseDTO {
    
    @NotNull(message = "Routine ID is required")
    private Long routineId;
 
    @NotNull(message = "Machine ID is required")
    private Long machineId;
}
