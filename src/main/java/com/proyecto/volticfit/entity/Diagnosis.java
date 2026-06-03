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
 * Entity representing a user's physical diagnosis/metrics.
 */
@Entity
@Data
@Table(name = "diagnostico")
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long idDiagnosis;

    @Column(name = "evaluador")
    private String evaluator;

    @Column(name = "observaciones")
    private String observations;

    @Column(name = "porcentaje_grasa")
    private Double fatPercentage;

    @Column(name = "masa_muscular")
    private Double muscleMass;

    @Column(name = "imc")
    private Double imc;

    @Column(name = "altura")
    private Double height;

    @Column(name = "peso")
    private Double weight;

    @Column(name = "sexo")
    private String gender;

    @Column(name = "edad")
    private Integer age;

    @Column(name = "fecha")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Users user;
}
