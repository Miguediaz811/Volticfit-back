package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Table(name = "Diagnostico")
@Data
public class Diagnostic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Integer id;

    private String evaluador;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "porcentaje_grasa")
    private Double porcentajeGrasa;

    @Column(name = "masa_muscular")
    private Double masaMuscular;

    private Double imc;

    private Double altura;

    private Double peso;

    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Users users;
}