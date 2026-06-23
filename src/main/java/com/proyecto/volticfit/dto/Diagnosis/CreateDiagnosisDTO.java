package com.proyecto.volticfit.dto.Diagnosis;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
/**
 * DTO para crear un diagnóstico, incluyendo detalles como el porcentaje de grasa, masa muscular, altura, peso, género y edad del usuario.
 */
@Data
public class CreateDiagnosisDTO {
     
    @NotNull(message = "User ID is required")
    private Long userId;
 
    private String evaluator;
    private String observations;
    private Double fatPercentage;
    private Double muscleMass;
 
    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.5", message = "Height must be at least 0.5 meters")
    @DecimalMax(value = "2.5", message = "Height must be at most 2.5 meters")
    private Double height;
 
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "10.0", message = "Weight must be at least 10 kg")
    @DecimalMax(value = "400.0", message = "Weight must be at most 400 kg")
    private Double weight;
 
    private String gender;
    private Integer age;
    private LocalDate date;
}
