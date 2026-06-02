package com.proyecto.volticfit.dto.Calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO representing the current connectivity and expiration status of a user's calendar integration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarStatusDTO {

    private Long userId;

    private boolean isConnected;

    private String provider;

    private LocalDateTime expiresAt;
}