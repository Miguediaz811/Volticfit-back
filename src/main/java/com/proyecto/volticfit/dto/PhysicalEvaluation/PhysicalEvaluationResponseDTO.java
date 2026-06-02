package com.proyecto.volticfit.dto.PhysicalEvaluation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for returning scheduled physical evaluation appointment details along with participant names.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalEvaluationResponseDTO {

    private Long id;            

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private String status;

    private String notes;

    private Long userId;        

    private String userFullName;

    private Long instructorId;  

    private String instructorFullName;
}