package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "soporte_tickets")
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private String consultaTexto;
    private String estado = "PENDIENTE"; // PENDIENTE, ESCALADA, RESUELTA
    private LocalDateTime fechaInicio = LocalDateTime.now();
}