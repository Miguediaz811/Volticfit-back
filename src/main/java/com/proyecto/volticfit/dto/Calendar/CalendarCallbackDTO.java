package com.proyecto.volticfit.dto.Calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for capturing authorization credentials sent back by the external OAuth2 provider callback.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarCallbackDTO {

    private Long userId;

    private String code;
}