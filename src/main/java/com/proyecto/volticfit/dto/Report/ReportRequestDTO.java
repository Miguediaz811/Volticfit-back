package com.proyecto.volticfit.dto.Report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO for capturing parameters required to filter, format, and generate administrative reports.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {

    private String type;       // e.g., "ATTENDANCE", "SANCTIONS", "REVENUE"

    private String format;     // e.g., "PDF", "EXCEL", "JSON"

    private LocalDate startDate;

    private LocalDate endDate;
}