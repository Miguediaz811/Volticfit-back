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

    @Column(name = "usuario_id")
    private Long usuarioId;

    private String accion;

    private String modulo;

    @Column(name = "status_http")
    private int statusHttp;
    
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora = LocalDateTime.now();
}