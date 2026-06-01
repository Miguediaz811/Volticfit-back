package com.proyecto.volticfit.entity;

import java.time.LocalDate;

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
 * Entidad que representa una restricción médica, incluyendo detalles como la descripción, tipo, fechas de inicio y fin, estado y su relación con un diagnóstico.
 */
@Entity
@Data
@Table(name = "RestriccionMedica")
public class MedicalRestriction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_restriccion")
    private Long idRestriction;
 
    @Column(name = "descripcion")
    private String description;
 
    @Column(name = "tipo")
    private String type;
 
    @Column(name = "fecha_inicio")
    private LocalDate startDate;
 
    @Column(name = "fecha_fin")
    private LocalDate endDate;
 
    @Column(name = "estado")
    private Boolean state = true;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private Diagnosis diagnosis;

}
