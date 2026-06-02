package com.proyecto.volticfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity representing a physical evaluation appointment scheduled between a student user and an instructor.
 */
@Entity
@Table(name = "EvaluacionFisica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhysicalEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion_fisica")
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDate date;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime startTime;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime endTime;

    @Column(name = "estado", length = 20)
    @Builder.Default
    private String status = "programada";

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instructor", nullable = false)
    private Users instructor;
}