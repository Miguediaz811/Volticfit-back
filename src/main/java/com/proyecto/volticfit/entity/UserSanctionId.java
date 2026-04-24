package com.proyecto.volticfit.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class UserSanctionId implements Serializable{
    private Long userId;
    private Long sanctionId;
}
