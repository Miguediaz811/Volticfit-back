package com.proyecto.volticfit.dto.Reservations;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

/**
 * DTO utilizado para reservar
 * el gimnasio completo.
 *
 * @author Miguel
 * @version 1.0
 */
@Data
public class CreateFullGymReservationDTO {

    /**
     * Fecha de la reserva.
     */
    private LocalDate reservationDate;

    /**
     * Hora de inicio.
     */
    private LocalTime startTime;

    /**
     * Hora final.
     */
    private LocalTime endTime;

    /**
     * Motivo de la reserva.
     */
    private String reason;
}