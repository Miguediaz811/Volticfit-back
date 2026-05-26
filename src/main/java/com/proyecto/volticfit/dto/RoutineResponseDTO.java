package com.proyecto.volticfit.dto;

import java.util.List;

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
