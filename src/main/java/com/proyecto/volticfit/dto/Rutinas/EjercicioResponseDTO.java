package com.proyecto.volticfit.dto.Rutinas;

import lombok.Data;

@Data
public class EjercicioResponseDTO {
    private Long idEjercicio;
    private String nombre;
    private Integer series;
    private Integer repeticiones;
    private Double pesoKg;
    private String notas;
}