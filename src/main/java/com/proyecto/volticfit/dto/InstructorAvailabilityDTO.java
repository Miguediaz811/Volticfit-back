package com.proyecto.volticfit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar la disponibilidad de un instructor
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorAvailabilityDTO {
    
    private Long instructorId;

    private String instructorName;

    private String startTime;

    private String endTime;
    
    private boolean available;

}
