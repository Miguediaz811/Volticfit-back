package com.proyecto.volticfit.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa una reserva, incluyendo detalles como la fecha, horas de inicio y fin, y el estado de la reserva.
 */
@Entity
@Data
@Table(name = "Reserva")
public class Reservation {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReservation;
 
    @Column(name = "fecha_reserva")
    private LocalDate date;
 
    @Column(name = "hora_inicio")
    private LocalTime startTime;
 
    @Column(name = "hora_fin")
    private LocalTime endTime;
 
    @Column(name = "estado")
    private Boolean state = true;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Users user;

}
