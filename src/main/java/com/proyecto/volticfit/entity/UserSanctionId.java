package com.proyecto.volticfit.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Clase que representa el ID compuesto de una relación entre un usuario y una sanción.
 */
@Embeddable
@Data
public class UserSanctionId implements Serializable{
    private Long userId;
    
    private Long sanctionId;
}
