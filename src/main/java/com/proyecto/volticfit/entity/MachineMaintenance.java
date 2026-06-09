package com.proyecto.volticfit.entity;


import java.time.LocalDate;

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
@Table(name = "mantenimiento")
@Data
public class MachineMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Long idMaintenance;

    @Column(name = "tipo")
    private String type;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "fecha")
    private LocalDate date;

    @Column(name = "responsable")
    private String responsible;

    @Column(name = "estado")
    private Boolean state;

    @ManyToOne
    @JoinColumn(name = "id_equipo")
    private Machine machine;
}
