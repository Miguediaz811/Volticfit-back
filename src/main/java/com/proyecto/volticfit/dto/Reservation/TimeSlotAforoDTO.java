package com.proyecto.volticfit.dto.Reservation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO con la información de aforo de una franja horaria específica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotAforoDTO {

    private String timeSlot;        // Ej: "06:00-08:00"
    private long currentOccupancy;  // Reservas activas en esa franja
    private int maxCapacity;        // Aforo máximo (20)
    private long availableSpots;    // Cupos disponibles
    private boolean isFull;         // true si aforo completo
    private List<String> registeredUsers; // Nombres de usuarios con reserva
}