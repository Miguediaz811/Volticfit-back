package com.proyecto.volticfit.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa la relación entre un usuario y una rutina, incluyendo detalles como la fecha de asignación y el estado de la asignación.
 */
@Entity
@Data
@Table(name = "Usuario_Rutina")
public class UserRoutine {
     
    @EmbeddedId
    private UserRoutineId id;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idUser")
    @JoinColumn(name = "id_usuario")
    private Users user;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idRoutine")
    @JoinColumn(name = "id_rutina")
    private Routine routine;
 
    @Column(name = "fecha_asignacion")
    private LocalDate assignmentDate;
 
    @Column(name = "estado")
    private Boolean state = true;
}