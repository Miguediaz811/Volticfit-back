package com.proyecto.volticfit.dto.ClinicalHistory;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para crear una historia clínica, incluyendo una descripción y la fecha de la consulta.
 */
@Data
public class CreateClinicalHistoryDTO {
 
    @NotBlank(message = "Description is required")
    @Size(max = 100, message = "La descripcion de la historia clinica no puede superar 100 caracteres")
    private String description;
 
    @NotNull(message = "Date is required")
    private LocalDate date;
}
