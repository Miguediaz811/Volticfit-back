package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "retroalimentacion")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private String categoria; // Sugerencia, Problema, Queja

    private String descripcion;

    private String codigoSeguimiento;

    private String estado = "Pendiente de revisión";
    
    private LocalDateTime fechaHora = LocalDateTime.now();

    @PrePersist
    public void generarCodigo() {
        this.codigoSeguimiento = "RET-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}