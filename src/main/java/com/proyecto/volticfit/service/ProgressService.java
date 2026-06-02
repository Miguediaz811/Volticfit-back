package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Progress.ProgressMetricDTO;
import com.proyecto.volticfit.dto.Progress.ProgressResponseDTO;
import com.proyecto.volticfit.entity.Diagnosis;
import com.proyecto.volticfit.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service class that handles the core business logic for aggregating, filtering, 
 * and chronologically sorting historical biological and diagnostic metrics for user evolution charts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressService {

    private final DiagnosisRepository diagnosisRepository;

    /**
     * Extracts multi-categorical diagnostic data for a specific user and constructs a consolidated 
     * progress wrapper with chronologically ordered metric streams.
     *
     * @param userId the unique identifier of the user whose physical metrics are being compiled
     * @return a {@link ProgressResponseDTO} containing sorted collections for weight, BMI, fat, and muscle metrics
     */
    public ProgressResponseDTO getUserProgressMetrics(Long userId) {
        log.info("Fetching progress metrics for user ID: {}", userId);

        List<Diagnosis> diagnostics = diagnosisRepository.findByUserIdUser(userId);

        List<ProgressMetricDTO> weightHistory = new ArrayList<>();
        List<ProgressMetricDTO> bmiHistory = new ArrayList<>();
        List<ProgressMetricDTO> fatPercentageHistory = new ArrayList<>();
        List<ProgressMetricDTO> muscleMassHistory = new ArrayList<>();

        for (Diagnosis diagnosis : diagnostics) {
            if (diagnosis.getDate() != null) {
                if (diagnosis.getWeight() != null) {
                    weightHistory.add(new ProgressMetricDTO(diagnosis.getDate(), diagnosis.getWeight().doubleValue()));
                }
                if (diagnosis.getImc() != null) {
                    bmiHistory.add(new ProgressMetricDTO(diagnosis.getDate(), diagnosis.getImc().doubleValue()));
                }
                if (diagnosis.getFatPercentage() != null) {
                    fatPercentageHistory.add(new ProgressMetricDTO(diagnosis.getDate(), diagnosis.getFatPercentage().doubleValue()));
                }
                if (diagnosis.getMuscleMass() != null) {
                    muscleMassHistory.add(new ProgressMetricDTO(diagnosis.getDate(), diagnosis.getMuscleMass().doubleValue()));
                }
            }
        }

        weightHistory.sort(Comparator.comparing(ProgressMetricDTO::getDate));
        bmiHistory.sort(Comparator.comparing(ProgressMetricDTO::getDate));
        fatPercentageHistory.sort(Comparator.comparing(ProgressMetricDTO::getDate));
        muscleMassHistory.sort(Comparator.comparing(ProgressMetricDTO::getDate));

        return ProgressResponseDTO.builder()
                .userId(userId)
                .weightHistory(weightHistory)
                .bmiHistory(bmiHistory)
                .fatPercentageHistory(fatPercentageHistory)
                .muscleMassHistory(muscleMassHistory)
                .build();
    }
}