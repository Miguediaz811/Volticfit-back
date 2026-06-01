package com.proyecto.volticfit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa una rutina, incluyendo detalles como el objetivo, duración, descripción, grupo muscular y nivel.
 */
@Entity
@Data
@Table(name = "Rutina")
public class Routine {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina")
    private Long idRoutine;
 
    @Column(name = "objetivo")
    private String objective;
 
    @Column(name = "duracion")
    private String duration;
 
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;
 
    @Column(name = "grupo_muscular")
    private String muscleGroup;
 
    @Column(name = "nivel")
    private String level;
}
