package com.proyecto.volticfit.dto.Progress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO containing structured streams of historical physical metrics for a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponseDTO {

    private Long userId;

    private List<ProgressMetricDTO> weightHistory;

    private List<ProgressMetricDTO> bmiHistory;

    private List<ProgressMetricDTO> fatPercentageHistory;

    private List<ProgressMetricDTO> muscleMassHistory;
}
