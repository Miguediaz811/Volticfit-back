package com.proyecto.volticfit.entity;


import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;



@Entity
@Table(name = "Mantenimiento")
@Data
public class MachineMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Long idMaintenance;

    private String type;

    private String description;

    private LocalDate date;

    private LocalTime time;

    private String responsible;

    private Boolean state;

    @ManyToOne
    @JoinColumn(name = "id_equipo")
    private Machine machine;
}