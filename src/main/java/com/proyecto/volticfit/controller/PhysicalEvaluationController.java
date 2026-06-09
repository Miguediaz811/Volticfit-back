package com.proyecto.volticfit.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.CreateEvaluationDTO;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.dto.InstructorAvailabilityDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.RescheduleEvaluationDTO;
import com.proyecto.volticfit.entity.PhysicalEvaluation;
import com.proyecto.volticfit.service.PhysicalEvaluationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para gestionar las evaluaciones físicas.
 */
@RestController
@RequestMapping("api/evaluations")
@RequiredArgsConstructor
@Tag(name = "Physical Evaluations", description = "Physical evaluation scheduling endpoints")
public class PhysicalEvaluationController {
    
    
    private final PhysicalEvaluationService evaluationService;
 
    @Operation(summary = "Get instructor availability for a date",
        responses = {
            @ApiResponse(responseCode = "200", description = "Availability retrieved successfully",
                content = @Content(schema = @Schema(implementation = InstructorAvailabilityDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date")
        }
    )
    @GetMapping("/availability")
    public ResponseEntity<Object> getAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<InstructorAvailabilityDTO> availability = evaluationService.getInstructorAvailability(date);
            if (availability.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No instructors available");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
 
    @Operation(summary = "Schedule a physical evaluation",
        responses = {
            @ApiResponse(responseCode = "201", description = "Evaluation scheduled successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Slot not available or invalid data")
        }
    )
    @RequiresRole({ RoleEnum.APRENDIZ, RoleEnum.FUNCIONARIO })
    @PostMapping
    public ResponseEntity<MessageResponseDTO> scheduleEvaluation(
            @Valid @RequestBody CreateEvaluationDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            MessageResponseDTO response = evaluationService.scheduleEvaluation(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
 
    @Operation(summary = "Get all evaluations (admin only)")
    @RequiresRole({ RoleEnum.ADMIN })
    @GetMapping("/all")
    public ResponseEntity<Object> getAllEvaluations() {
        try {
            List<PhysicalEvaluation> evaluations = evaluationService.getAllEvaluations();
            if (evaluations.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No evaluations found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(evaluations);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Get all evaluations for the authenticated user")
    @GetMapping("/my-evaluations")
    public ResponseEntity<Object> getUserEvaluations(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            List<PhysicalEvaluation> evaluations = evaluationService.getUserEvaluations(userId);
            if (evaluations.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No evaluations found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(evaluations);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Reschedule a physical evaluation",
        responses = {
            @ApiResponse(responseCode = "200", description = "Evaluation rescheduled successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Slot not available or invalid data"),
            @ApiResponse(responseCode = "404", description = "Evaluation not found")
        }
    )
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<MessageResponseDTO> recheduleEvaluation(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleEvaluationDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long requesterId  = (Long) httpRequest.getAttribute("userId");
            String role = (String) httpRequest.getAttribute("role");
            MessageResponseDTO response = evaluationService.rescheduleEvaluation(id, request, requesterId, role);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Cancel a physical evaluation",
        responses = {
            @ApiResponse(responseCode = "200", description = "Evaluation cancelled successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Evaluation not found")
        }
    )
    @PutMapping("/{id}/cancel")
    public ResponseEntity<MessageResponseDTO> cancelEvaluation(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            Long requesterId = (Long) httpRequest.getAttribute("userId");
            String role = (String) httpRequest.getAttribute("role");
            MessageResponseDTO response = evaluationService.cancelEvaluation(id, requesterId, role);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}