package com.proyecto.volticfit.dto.Attendance;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning a single attendance record in paginated list queries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListResponseDTO {

    private Long id;

    private String fullName;

    private String docNumber;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private String registrationType;
}