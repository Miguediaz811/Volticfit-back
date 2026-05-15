package com.proyecto.volticfit.dto.Rutinas;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RutinaRequestDTO {

    @NotBlank(message = "El nombre de la rutina es obligatorio")
    private String nombre;

    private String descripcion;

    @NotEmpty(message = "La rutina debe tener al menos un ejercicio")
    @Valid
    private List<EjercicioDTO> ejercicios;
}