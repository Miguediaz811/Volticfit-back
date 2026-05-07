package com.proyecto.volticfit.dto.Reservation;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta con el aforo actual por franja horaria en una fecha.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AforoResponseDTO {

    private LocalDate date;
    private List<TimeSlotAforoDTO> slots;
}