package com.proyecto.volticfit.dto.ClinicalHistory;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para crear una historia clínica, incluyendo una descripción y la fecha de la consulta.
 */
@Data
public class CreateClinicalHistoryDTO {
 
    @NotBlank(message = "Description is required")
    private String description;
 
    @NotNull(message = "Date is required")
    private LocalDate date;
}
