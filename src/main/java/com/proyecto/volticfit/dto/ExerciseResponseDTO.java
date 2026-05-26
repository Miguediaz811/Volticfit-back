package com.proyecto.volticfit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
