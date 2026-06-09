package com.proyecto.volticfit.dto.Exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO para responder con los detalles de un ejercicio, incluyendo su estado de completitud.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponseDTO {
    
    private String name;

    private Integer sets;

    private Integer reps;

    private String description;

    private String gifUrl;
    
    private boolean completed;

}
