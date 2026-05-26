package com.proyecto.volticfit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Ejercicios")
public class Exercise {
 
    @EmbeddedId
    private ExerciseId id;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idRoutine")
    @JoinColumn(name = "id_rutina")
    private Routine routine;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idMachine")
    @JoinColumn(name = "id_maquina")
    private Machine machine;
 
    @Column(name = "nombre")
    private String name;
 
    @Column(name = "series")
    private Integer sets;
 
    @Column(name = "repeticiones")
    private Integer reps;
 
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;
 
    @Column(name = "gif_url")
    private String gifUrl;
}
