package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Recommendation.RecommendationResponseDTO;
import com.proyecto.volticfit.entity.Diagnosis;
import com.proyecto.volticfit.entity.Recommendation;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.DiagnosisRepository;
import com.proyecto.volticfit.repository.RecommendationRepository;
import com.proyecto.volticfit.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class that handles the core business logic for the automated recommendation engine,
 * evaluating historical user diagnostics and compiling personalized fitness guidelines.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final UsersRepository usersRepository;

    /**
     * Retrieves all personalized recommendations issued for a user, ordered from newest to oldest.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link RecommendationResponseDTO} objects matching the user's history
     */
    public List<RecommendationResponseDTO> getUserRecommendations(Long userId) {
        log.info("Retrieving recommendations for user ID: {}", userId);
        return recommendationRepository.findByUser_IdUserOrderByCreationDateDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Triggers the internal rule subsystem to analyze the latest available body metrics and 
     * dynamically compile tailored conditional fitness plans and classifications.
     *
     * @param userId the unique identifier of the user to process
     * @return a generated and persisted {@link RecommendationResponseDTO} framework
     * @throws RuntimeException if the target user infrastructure record does not exist
     */
    public RecommendationResponseDTO generatePersonalizedRecommendation(Long userId) {
        log.info("Starting automated recommendation engine for user ID: {}", userId);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<Diagnosis> diagnostics = diagnosisRepository.findByUserIdUser(userId);
        
        StringBuilder recommendationText = new StringBuilder();
        String recommendationType = "General Fitness";

        if (diagnostics.isEmpty()) {
            recommendationText.append("Welcome to VolticFit! Please schedule your first physical evaluation to generate custom training guidelines.");
            recommendationType = "Initial Onboarding";
        } else {
            Diagnosis latestDiagnosis = diagnostics.stream()
                    .max((d1, d2) -> d1.getDate().compareTo(d2.getDate()))
                    .orElse(diagnostics.get(0));

            double bmi = latestDiagnosis.getImc() != null ? latestDiagnosis.getImc().doubleValue() : 22.0;

            if (bmi >= 25.0) {
                recommendationText.append("Focus on high-intensity interval training (HIIT) combined with safe deficit conditioning. ");
                recommendationType = "Cardiovascular & Weight Management";
            } else if (bmi < 18.5) {
                recommendationText.append("Focus on progressive overload hypertrophy routines and structured strength building blocks. ");
                recommendationType = "Muscle Mass Gain";
            } else {
                recommendationText.append("Maintain an optimized balanced training routine blending multi-joint strength movements and metabolic conditioning. ");
                recommendationType = "Physical Optimization";
            }

            recommendationText.append("Remember to strictly follow target rep ranges and maintain proper execution form.");
        }

        Recommendation recommendation = Recommendation.builder()
                .user(user)
                .description(recommendationText.toString())
                .type(recommendationType)
                .creationDate(LocalDate.now())
                .status(true)
                .build();

        Recommendation savedRecommendation = recommendationRepository.save(recommendation);
        log.info("Successfully generated and persisted recommendation ID: {}", savedRecommendation.getId());

        return mapToDTO(savedRecommendation);
    }

    /**
     * Transforms an internal structural {@link Recommendation} database entity record 
     * into an external data transfer object layer wrapper.
     *
     * @param recommendation the internal source entity instance to extract values from
     * @return a mapped and configured {@link RecommendationResponseDTO} instance
     */
    private RecommendationResponseDTO mapToDTO(Recommendation recommendation) {
        return RecommendationResponseDTO.builder()
                .id(recommendation.getId())
                .userId(recommendation.getUser().getIdUser())
                .description(recommendation.getDescription())
                .type(recommendation.getType())
                .creationDate(recommendation.getCreationDate())
                .status(recommendation.getStatus())
                .build();
    }
}