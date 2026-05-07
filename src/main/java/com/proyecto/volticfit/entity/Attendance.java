package com.proyecto.volticfit.entity;

import java.time.LocalDate;
import java.time.LocalTime;

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
 * Entidad que representa un registro de asistencia en el sistema.
 * Puede ser de tipo ENTRADA o SALIDA, y puede ser manual o automático.
 */
@Entity
@Data
@Table(name = "Asistencia")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Long idAttendance;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Users user;

    @Column(name = "tipo", nullable = false, length = 10)
    private String type; // ENTRADA o SALIDA

    @Column(name = "es_manual", nullable = false)
    private Boolean isManual;

    @Column(name = "fecha", nullable = false)
    private LocalDate date;

    @Column(name = "hora", nullable = false)
    private LocalTime time;

    @Column(name = "motivo", columnDefinition = "TEXT")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "registrado_por", nullable = false)
    private Users registeredBy;
}