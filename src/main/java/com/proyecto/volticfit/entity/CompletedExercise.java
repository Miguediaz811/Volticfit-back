package com.proyecto.volticfit.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a completed exercise by a user.
 */
@Entity
@Data
@Table(name = "Ejercicio_Completado")
public class CompletedExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_completado")
    private Long idCompleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rutina", nullable = false)
    private Routine routine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_maquina", nullable = false)
    private Machine machine;

    @Column(name = "fecha_completado", nullable = false)
    private LocalDateTime completedAt;
}