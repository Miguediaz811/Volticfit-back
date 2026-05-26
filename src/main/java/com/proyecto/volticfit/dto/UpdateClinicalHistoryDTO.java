package com.proyecto.volticfit.dto;
 
import java.time.LocalDate;
 
import lombok.Data;
 
/**
 * DTO for updating a clinical history entry.
 */
@Data
public class UpdateClinicalHistoryDTO {

    private String description;
    
    private LocalDate date;
}
