package com.proyecto.volticfit.dto.Reservations;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para crear una reserva, incluyendo la fecha y la hora de inicio de la reserva.
 */
@Data
public class CreateReservationDTO {
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;
}
