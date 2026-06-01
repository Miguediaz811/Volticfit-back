package com.proyecto.volticfit.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad de evaluación Física
 */
@Entity
@Data
@Table (name = "EvaluacionFisica")
public class PhysicalEvaluation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion_fisica")
    private Long IdEvaluation;
 
    @Column(name = "fecha", nullable = false)
    private LocalDate date;
 
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime startTime;
 
    @Column(name = "hora_fin", nullable = false)
    private LocalTime endTime;
 
    @Column(name = "estado")
    private String status = "programada"; // programada, cancelada, completada
 
    @Column(name = "notas")
    private String notes;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Users user;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_instructor", nullable = false)
    private Users instructor;
}
