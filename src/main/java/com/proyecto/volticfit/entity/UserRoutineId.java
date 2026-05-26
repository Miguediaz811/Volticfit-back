package com.proyecto.volticfit.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class UserRoutineId implements Serializable {

    private Long idUser;
    
    private Long idRoutine;
}
