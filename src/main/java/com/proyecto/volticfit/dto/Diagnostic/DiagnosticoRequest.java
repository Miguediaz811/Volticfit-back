package com.proyecto.volticfit.dto.Diagnostic;

import lombok.Data;

@Data
public class DiagnosticoRequest {

    private String evaluador;

    private String observaciones;

    private Double porcentajeGrasa;

    private Double masaMuscular;

    private Double altura;

    private Double peso;

    private Integer idUsuario;
}
