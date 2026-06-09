package com.proyecto.volticfit.dto.MedicalRestiction;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
/**
 * DTO para crear una restricción médica
 */
@Data
public class CreateMedicalRestrictionDTO {
    
    @NotNull(message = "Diagnosis ID is required")
    private Long diagnosisId;
 
    @NotBlank(message = "Description is required")
    private String description;
 
    @NotBlank(message = "Type is required")
    private String type;
 
    private LocalDate startDate;
    private LocalDate endDate;
}
