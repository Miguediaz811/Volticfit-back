package com.proyecto.volticfit.dto.Recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO representing a personalized fitness or nutritional recommendation generated for a specific user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDTO {

    private Integer id;

    private Long userId;

    private String description;

    private String type;

    private LocalDate creationDate;

    private Boolean status;
}