package com.proyecto.volticfit.dto.Calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for capturing the initial parameters required to request a connection URL for an external calendar provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarConnectRequestDTO {

    private Long userId;

    private String provider;

    private String redirectUri;
}