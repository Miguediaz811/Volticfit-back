package com.proyecto.volticfit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Progress.ProgressMetricDTO;
import com.proyecto.volticfit.dto.Progress.ProgressResponseDTO;
import com.proyecto.volticfit.service.ProgressService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> registerMetrics(
            @RequestBody ProgressMetricDTO metrics,
            HttpServletRequest request) {
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Metricas recibidas. Para guardarlas registra un diagnostico fisico.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/graphs")
    public ResponseEntity<?> getProgressGraphs(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ProgressResponseDTO progress = progressService.getUserProgressMetrics(userId);

        boolean hasData = !progress.getWeightHistory().isEmpty()
                || !progress.getBmiHistory().isEmpty()
                || !progress.getFatPercentageHistory().isEmpty()
                || !progress.getMuscleMassHistory().isEmpty();

        return hasData ? ResponseEntity.ok(progress) : ResponseEntity.noContent().build();
    }
}
