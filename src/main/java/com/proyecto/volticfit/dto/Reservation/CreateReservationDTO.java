package com.proyecto.volticfit.dto.Reservation;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para crear una reserva en una franja horaria.
 */
@Data
public class CreateReservationDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotNull(message = "La fecha de reserva es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationDate;

    @NotBlank(message = "La franja horaria es obligatoria")
    private String timeSlot; // Ej: "06:00-08:00"
}