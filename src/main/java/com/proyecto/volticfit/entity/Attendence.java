package com.proyecto.volticfit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa registros de asistencia (adaptada a estructura existente).
 */
@Entity
@Data
@Table(name = "Usuario") 
public class Attendence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(name = "nombres")
    private String names;

    @Column(name = "apellidos")
    private String surnames;

    @Column(name = "num_doc")
    private String docNum;

    @Column(name = "rol")
    private String role;
}