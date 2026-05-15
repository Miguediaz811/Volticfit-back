package com.proyecto.volticfit.dto.Metricas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MetricaRequestDTO {

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor a 0")
    private Double pesoKg;

    @NotNull(message = "La estatura es obligatoria")
    @Positive(message = "La estatura debe ser mayor a 0")
    private Double estaturaCm;

    private Double porcentajeGrasa;
    private Double cinturaCm;
    private Double caderaCm;
    private Double pechoCm;
    private Double brazoCm;
}