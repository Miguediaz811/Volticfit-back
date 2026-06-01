package com.proyecto.volticfit.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa una máquina de gimnasio, incluyendo detalles como el nombre, tipo, fecha de registro y estado.
 */
@Entity
@Data
@Table(name = "Maquina")
public class Machine {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_maquina")
    private Long idMachine;
 
    @Column(name = "nombre")
    private String name;
 
    @Column(name = "tipo")
    private String type;
 
    @Column(name = "fecha_registro")
    private LocalDate registrationDate;
 
    @Column(name = "estado")
    private Boolean state;
}
