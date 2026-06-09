package com.proyecto.volticfit.dto.MedicalRestiction;
 
import java.time.LocalDate;
 
import lombok.Data;
 
/**
 * DTO for updating a medical restriction.
 */
@Data
public class UpdateMedicalRestrictionDTO {

    private String description;

    private String type;

    private LocalDate startDate;

    private LocalDate endDate;
    
    private Boolean state;
}