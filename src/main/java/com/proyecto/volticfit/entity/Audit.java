package com.proyecto.volticfit.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa un log de auditoría para registros manuales de asistencia.
 */
@Entity
@Data
@Table(name = "Auditoria")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAudit;

    @ManyToOne
    @JoinColumn(name = "id_admin", nullable = false)
    private Users admin;

    @ManyToOne
    @JoinColumn(name = "id_usuario_afectado", nullable = false)
    private Users affectedUser;

    @Column(name = "accion", nullable = false, length = 100)
    private String action;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime dateTime;
}