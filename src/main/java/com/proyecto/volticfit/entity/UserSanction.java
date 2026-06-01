package com.proyecto.volticfit.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa la relación entre un usuario y una sanción, incluyendo detalles como la fecha de asignación y el estado de la sanción.
 */
@Entity
@Data
@Table(name = "usuario_sancion")
public class UserSanction {

    @EmbeddedId
    private UserSanctionId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "id_usuario")
    private Users user;

    @ManyToOne
    @MapsId("sanctionId")
    @JoinColumn(name = "id_sancion")
    private Sanction sanction;
}
