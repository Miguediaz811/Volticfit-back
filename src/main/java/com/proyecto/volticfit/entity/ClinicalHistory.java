package com.proyecto.volticfit.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * Entidad que representa el historial clínico de un usuario, incluyendo detalles como la descripción del historial, la fecha y la relación con el usuario.
 */
@Entity
@Data
@Table(name = "HistorialClinico")
public class ClinicalHistory {
     
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistory;
 
    @Column(name = "descripcion", nullable = false)
    private String description;
 
    @Column(name = "fecha", nullable = false)
    private LocalDate date;
 
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Users user;
}