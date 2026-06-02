package com.proyecto.volticfit.dto.PhysicalEvaluation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for capturing the data required to schedule or request a new physical evaluation appointment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalEvaluationRequestDTO {

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private String notes;

    private Long userId;        

    private Long instructorId;  
}