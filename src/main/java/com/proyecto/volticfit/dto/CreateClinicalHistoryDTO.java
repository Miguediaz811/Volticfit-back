package com.proyecto.volticfit.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateClinicalHistoryDTO {
 
    @NotBlank(message = "Description is required")
    private String description;
 
    @NotNull(message = "Date is required")
    private LocalDate date;
}
