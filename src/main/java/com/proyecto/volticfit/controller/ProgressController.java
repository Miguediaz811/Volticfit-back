package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.Progress.ProgressResponseDTO;
import com.proyecto.volticfit.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ProgressResponseDTO> getUserProgress(@PathVariable Long userId) { // Cambiado a Long
        ProgressResponseDTO progressData = progressService.getUserProgressMetrics(userId);
        return ResponseEntity.ok(progressData);
    }
}