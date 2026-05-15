package com.proyecto.volticfit.dto.Rutinas;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RutinaVersionResponseDTO {
    private Long idVersion;
    private Integer numeroVersion;
    private String nombreRutina;
    private String descripcionRutina;
    private String ejerciciosJson;
    private LocalDateTime fechaGuardado;
    private String guardadoPor;
    private Boolean esRestauracion;
    private Integer versionOrigenRestauracion;
}