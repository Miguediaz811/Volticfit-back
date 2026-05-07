package com.proyecto.volticfit.dto.Attendance;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

/**
 * DTO de respuesta con los datos de un registro de asistencia.
 */
@Data
public class AttendanceResponseDTO {

    private Long idAttendance;
    private Long userId;
    private String userName;
    private String type;
    private Boolean isManual;
    private LocalDate date;
    private LocalTime time;
    private String reason;
    private String registeredByName;
}