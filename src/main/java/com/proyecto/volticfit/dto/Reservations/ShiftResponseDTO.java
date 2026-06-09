package com.proyecto.volticfit.dto.Reservations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para responder con los detalles de un turno, incluyendo la hora de inicio, la hora de fin, los cupos disponibles, 
 * los cupos totales y si el turno está disponible para reservar. 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftResponseDTO {

    private String startTime;

    private String endTime;

    private int availableSpots;

    private int totalSpots;
    
    private boolean available;
}
