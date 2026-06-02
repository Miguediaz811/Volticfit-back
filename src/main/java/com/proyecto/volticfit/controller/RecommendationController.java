package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.Recommendation.RecommendationResponseDTO;
import com.proyecto.volticfit.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for retrieving and generating automated fitness and training recommendations.
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Retrieves all historical personalized recommendations issued for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a ResponseEntity containing a list of previous recommendations
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecommendationResponseDTO>> getRecommendationsByUserId(@PathVariable Long userId) {
        List<RecommendationResponseDTO> recommendations = recommendationService.getUserRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Triggers the automated rule engine to evaluate physical data and generate a new personalized recommendation.
     *
     * @param userId the unique identifier of the user for whom the recommendation will be generated
     * @return a ResponseEntity containing the newly generated and persisted recommendation details
     */
    @PostMapping("/generate/user/{userId}")
    public ResponseEntity<RecommendationResponseDTO> generateUserRecommendation(@PathVariable Long userId) {
        RecommendationResponseDTO newRecommendation = recommendationService.generatePersonalizedRecommendation(userId);
        return ResponseEntity.ok(newRecommendation);
    }
}