package com.proyecto.volticfit.dto.Calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO representing the current external calendar integration status and token expiration details for a specific user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarStatusDTO {

    private Long userId;

    private Boolean isConnected;

    private String provider;

    private LocalDateTime expiresAt;
}