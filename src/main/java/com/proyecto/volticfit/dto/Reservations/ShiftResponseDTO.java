package com.proyecto.volticfit.dto.Reservations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
