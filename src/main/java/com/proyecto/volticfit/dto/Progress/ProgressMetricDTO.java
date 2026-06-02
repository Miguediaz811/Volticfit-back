package com.proyecto.volticfit.dto.Progress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO representing a single timeline point for physical evolution charts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressMetricDTO {

    private LocalDate date;

    private Double value;
}