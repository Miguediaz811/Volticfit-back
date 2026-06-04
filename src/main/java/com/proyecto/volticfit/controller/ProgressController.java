package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Progress.ProgressMetricDTO;
import com.proyecto.volticfit.dto.Progress.ProgressResponseDTO;
import com.proyecto.volticfit.service.ProgressService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> registerMetrics(
            @RequestBody ProgressMetricDTO metrics, 
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("userId");
        
        // Asumiendo que tu servicio tiene este método
        // progressService.saveMetrics(userId, metrics);
        
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Métricas registradas exitosamente. IMC calculado automáticamente.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/graphs")
    public ResponseEntity<?> getProgressGraphs(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        // Asumiendo que tu servicio tiene este método
        // List<ProgressResponseDTO> history = progressService.getUserProgress(userId);
        List<ProgressResponseDTO> history = List.of(); // Cambiar por la llamada real al servicio
        
        if (history.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(history);
    }
}