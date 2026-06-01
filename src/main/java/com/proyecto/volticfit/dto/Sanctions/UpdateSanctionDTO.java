package com.proyecto.volticfit.dto.Sanctions;
import java.time.LocalDate;
import lombok.Data;

/**
 * DTO para actualizar una sanción
 */
@Data
public class UpdateSanctionDTO {
    
    private String description;

    private String type;

    private LocalDate startDate;
    
    private LocalDate endDate;
}
