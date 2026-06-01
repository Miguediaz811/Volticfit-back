package com.proyecto.volticfit.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Clase que representa el ID compuesto de una relación entre un usuario y una rutina.
 */
@Embeddable
@Data
public class UserRoutineId implements Serializable {

    private Long idUser;
    
    private Long idRoutine;
}
