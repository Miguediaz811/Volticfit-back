package com.proyecto.volticfit.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ExerciseId implements Serializable {

    private Long idRoutine;

    private Long idMachine;
}
