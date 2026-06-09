package com.proyecto.volticfit.dto.Routine;
/**
 * DTO para responder con los detalles de una rutina, incluyendo sus ejercicios asociados.
 */
import java.util.List;

import com.proyecto.volticfit.dto.Exercise.ExerciseResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineResponseDTO {

    private Long routineId;
    private String objective;
    private String duration;
    private String description;
    private String muscleGroup;
    private String level;
    private boolean isPersonalized;
    private String warningMessage;
    private List<ExerciseResponseDTO> exercises;
    
}
