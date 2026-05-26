package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.volticfit.dto.CreateClinicalHistoryDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.UpdateClinicalHistoryDTO;
import com.proyecto.volticfit.entity.ClinicalHistory;
import com.proyecto.volticfit.service.ClinicalHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller for clinical history management.
 * Accessible by both ADMIN and the user themselves.
 */
@RestController
@RequestMapping("/api/clinical-history")
@RequiredArgsConstructor
@Tag(name = "Clinical History", description = "Clinical history management endpoints")
public class ClinicalHistoryController {

    private final ClinicalHistoryService clinicalHistoryService;

    @Operation(summary = "Create a clinical history entry",
        responses = {
            @ApiResponse(responseCode = "201", description = "Entry created successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error creating entry")
        }
    )
    @PostMapping
    public ResponseEntity<MessageResponseDTO> create(
            @Valid @RequestBody CreateClinicalHistoryDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long requesterId = (Long) httpRequest.getAttribute("userId");
            String role = (String) httpRequest.getAttribute("role");
            // Admin can specify target user via query param, user creates for themselves
            String targetUserIdParam = httpRequest.getParameter("userId");
            Long targetUserId = targetUserIdParam != null ? Long.parseLong(targetUserIdParam) : requesterId;
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(clinicalHistoryService.create(request, requesterId, role, targetUserId));
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Get clinical history for a user")
    @GetMapping
    public ResponseEntity<Object> getHistory(HttpServletRequest httpRequest) {
        try {
            Long requesterId = (Long) httpRequest.getAttribute("userId");
            String role = (String) httpRequest.getAttribute("role");
            String targetUserIdParam = httpRequest.getParameter("userId");
            Long targetUserId = targetUserIdParam != null ? Long.parseLong(targetUserIdParam) : requesterId;
            List<ClinicalHistory> history = clinicalHistoryService.getByUser(requesterId, role, targetUserId);
            if (history.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No clinical history found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Update a clinical history entry")
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> update(
            @PathVariable Long id,
            @RequestBody UpdateClinicalHistoryDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long requesterId = (Long) httpRequest.getAttribute("userId");
            String role = (String) httpRequest.getAttribute("role");
            return ResponseEntity.ok(clinicalHistoryService.update(id, request, requesterId, role));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }

    @Operation(summary = "Delete a clinical history entry")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> delete(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            Long requesterId = (Long) httpRequest.getAttribute("userId");
            String role = (String) httpRequest.getAttribute("role");
            return ResponseEntity.ok(clinicalHistoryService.delete(id, requesterId, role));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }
}