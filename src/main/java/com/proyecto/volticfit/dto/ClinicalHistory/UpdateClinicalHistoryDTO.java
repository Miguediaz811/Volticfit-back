package com.proyecto.volticfit.dto.ClinicalHistory;
 
import java.time.LocalDate;
 
import jakarta.validation.constraints.Size;
import lombok.Data;
 
/**
 * DTO for updating a clinical history entry.
 */
@Data
public class UpdateClinicalHistoryDTO {

    @Size(max = 100, message = "La descripcion de la historia clinica no puede superar 100 caracteres")
    private String description;
    
    private LocalDate date;
}
