package com.proyecto.volticfit.dto.Attendance;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO para registrar manualmente una asistencia (entrada o salida).
 */
@Data
public class CreateAttendanceDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotBlank(message = "El tipo de registro es obligatorio")
    @Pattern(regexp = "ENTRADA|SALIDA", message = "El tipo debe ser ENTRADA o SALIDA")
    private String type;

    @NotNull(message = "La fecha es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "La hora es obligatoria")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime time;

    @NotBlank(message = "El motivo del registro manual es obligatorio")
    private String reason;

    /** Contraseña adicional requerida cuando hay más de 15 registros manuales */
    private String securityPassword;
}