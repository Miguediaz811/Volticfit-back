package com.proyecto.volticfit.dto.Rutinas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EjercicioDTO {

    @NotBlank(message = "El nombre del ejercicio es obligatorio")
    private String nombre;

    @NotNull(message = "Las series son obligatorias")
    private Integer series;

    @NotNull(message = "Las repeticiones son obligatorias")
    private Integer repeticiones;

    private Double pesoKg;
    private String notas;
}