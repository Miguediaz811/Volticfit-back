package com.proyecto.volticfit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mostrar asistencias del usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendenceResponseDTO {

    private String fullName;

    private String docNum;
    
}