package com.proyecto.volticfit.dto.Progress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO representing a single data point containing a chronological date and its corresponding numerical metric value.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressMetricDTO {

    private LocalDate date;

    private Double value;
}