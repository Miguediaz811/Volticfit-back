package com.proyecto.volticfit.entity;

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

@Entity
@Data
@Table(name = "ejercicio")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejercicio")
    private Long idEjercicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "series", nullable = false)
    private Integer series;

    @Column(name = "repeticiones", nullable = false)
    private Integer repeticiones;

    @Column(name = "peso_kg")
    private Double pesoKg;

    @Column(name = "notas")
    private String notas;
}