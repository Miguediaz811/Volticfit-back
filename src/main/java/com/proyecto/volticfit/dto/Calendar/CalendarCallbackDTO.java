package com.proyecto.volticfit.dto.Calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for capturing the authorization callback parameters sent by an external calendar provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarCallbackDTO {

    private Long userId;

    private String code;

    private String state;

    private String provider;
}