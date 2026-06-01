package com.proyecto.volticfit.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para reprogramar una evaluación
 */
@Data
public class RescheduleEvaluationDTO {
    
    @NotNull(message = "Date is required")
    private LocalDate date;
 
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
 
    @NotNull(message = "Instructor ID is required")
    private Long instructorId;

}
