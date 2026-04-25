package com.proyecto.volticfit.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


/**
 * Entidad sanción representada en el sistema
 */
@Entity
@Data
@Table(name = "sanciones")
public class Sanction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sancion")
    private Long idSanction;

    @Column(name = "descripcion")
    private String description;
 
    @Column(name = "tipo")
    private String type;
 
    @Column(name = "fecha_inicio")
    private LocalDate startDate;
 
    @Column(name = "fecha_fin")
    private LocalDate endDate;
 
    @Column(name = "estado")
    private Boolean state;
}
