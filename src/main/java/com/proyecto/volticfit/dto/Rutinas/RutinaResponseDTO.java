package com.proyecto.volticfit.dto.Rutinas;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class RutinaResponseDTO {
    private Long idRutina;
    private String nombre;
    private String descripcion;
    private String correoUsuario;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private String modificadoPor;
    private Integer versionActual;
    private List<EjercicioResponseDTO> ejercicios;
}