package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Exercise.CompleteExerciseDTO;
import com.proyecto.volticfit.dto.Routine.RoutineResponseDTO;
import com.proyecto.volticfit.entity.UserRoutine;
import com.proyecto.volticfit.service.RoutineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller for AI-generated routine management.
 */
@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
@Tag(name = "Routines", description = "AI-generated personalized routine endpoints")
public class RoutineController {

    // Inyectamos el servicio de Routine para manejar la lógica de negocio relacionada con las rutinas generadas por IA
    private final RoutineService routineService;

    @Operation(summary = "Generate a new personalized routine using Gemini AI",
        responses = {
            @ApiResponse(responseCode = "201", description = "Routine generated successfully",
                content = @Content(schema = @Schema(implementation = RoutineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Too soon to generate a new routine")
        }
    )
    @PostMapping("/generate")
    public ResponseEntity<Object> generateRoutine(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            RoutineResponseDTO response = routineService.generateRoutine(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Get the active routine for the user")
    @GetMapping("/active")
    public ResponseEntity<Object> getActiveRoutine(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return ResponseEntity.ok(routineService.getActiveRoutine(userId));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Operation(summary = "Get routine history for the user")
    @GetMapping("/history")
    public ResponseEntity<Object> getRoutineHistory(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<UserRoutine> history = routineService.getRoutineHistory(userId);
            if (history.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No routine history found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Mark a single exercise as completed")
    @PostMapping("/complete-exercise")
    public ResponseEntity<MessageResponseDTO> completeExercise(
            @Valid @RequestBody CompleteExerciseDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            return ResponseEntity.ok(routineService.completeExercise(request, userId));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Mark all exercises in a routine as completed")
    @PostMapping("/{routineId}/complete-all")
    public ResponseEntity<MessageResponseDTO> completeRoutine(
            @PathVariable Long routineId,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return ResponseEntity.ok(routineService.completeRoutine(routineId, userId));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}