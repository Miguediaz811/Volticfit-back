package com.proyecto.volticfit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KpiDTO {

    private String nombreIndicador;

    private Double valor;
    
    private String descripcion;
}