package com.proyecto.volticfit.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Notification {

    private Long id;

    private String titulo;

    private String mensaje;

    private String destinatario;

    /*
        true = enviada
        false = error
     */
    private Boolean estado;

    private Integer reintentos;

    private LocalDateTime fechaCreacion;
}