package com.proyecto.volticfit.dto.Rutinas;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestaurarVersionRequestDTO {

    @NotNull(message = "El número de versión es obligatorio")
    private Integer numeroVersion;
}