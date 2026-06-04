package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "registro_actividad")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private String accion;

    private String modulo;

    private int statusHttp;
    
    private LocalDateTime fechaHora = LocalDateTime.now();
}