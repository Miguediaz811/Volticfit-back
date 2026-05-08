package com.proyecto.volticfit.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mostrar horarios disponibles.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDTO {

    private Long reservationId;

    private LocalDate reservationDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean state;

    private String userName;
}