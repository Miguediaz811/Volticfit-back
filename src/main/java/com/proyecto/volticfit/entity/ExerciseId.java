package com.proyecto.volticfit.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Clase que representa el ID compuesto de un ejercicio.
 */
@Embeddable
@Data
public class ExerciseId implements Serializable {

    private Long idRoutine;

    private Long idMachine;
}
