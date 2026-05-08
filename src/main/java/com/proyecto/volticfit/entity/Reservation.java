package com.proyecto.volticfit.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa una reserva de horario en VolticFit.
 */
@Entity
@Data
@Table(name = "Reserva")
public class Reservation {

    /**
     * Identificador único de la reserva.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReservation;

    /**
     * Fecha de la reserva.
     */
    @Column(name = "fecha_reserva")
    private LocalDate reservationDate;

    /**
     * Hora de inicio de la reserva.
     */
    @Column(name = "hora_inicio")
    private LocalTime startTime;

    /**
     * Hora de finalización de la reserva.
     */
    @Column(name = "hora_fin")
    private LocalTime endTime;

    /**
     * Estado de la reserva.
     *
     * true = activa
     * false = cancelada
     */
    @Column(name = "estado")
    private Boolean state = true;

    /**
     * Usuario asociado a la reserva.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Users user;
}