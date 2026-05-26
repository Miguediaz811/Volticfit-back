package com.proyecto.volticfit.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateDiagnosisDTO {
     
    @NotNull(message = "User ID is required")
    private Long userId;
 
    private String evaluator;
    private String observations;
    private Double fatPercentage;
    private Double muscleMass;
 
    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    private Double height;
 
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;
 
    private String gender;
    private Integer age;
    private LocalDate date;
}
