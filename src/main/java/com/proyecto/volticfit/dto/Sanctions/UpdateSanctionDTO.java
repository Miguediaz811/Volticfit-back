package com.proyecto.volticfit.dto.Sanctions;
/**
 * DTO para actualizar una sanción
 */

import java.time.LocalDate;


import lombok.Data;

@Data
public class UpdateSanctionDTO {
    
    private String description;

    private String type;

    private LocalDate startDate;
    
    private LocalDate endDate;
}
