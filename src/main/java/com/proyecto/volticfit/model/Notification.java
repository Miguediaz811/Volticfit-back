package com.proyecto.volticfit.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notification {

    private Long id;

    private String titulo;

    private String mensaje;

    private String tipo;

    private Long usuarioDestinoId;

    private String usuarioDestinoNombre;

    private boolean leida = false;

    private LocalDateTime fechaEnvio = LocalDateTime.now();
    
    private LocalDateTime fechaExpiracion;
}