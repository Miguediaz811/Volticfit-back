package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Diagnosis.CreateDiagnosisDTO;
import com.proyecto.volticfit.dto.MedicalRestiction.CreateMedicalRestrictionDTO;
import com.proyecto.volticfit.dto.MedicalRestiction.UpdateMedicalRestrictionDTO;
import com.proyecto.volticfit.entity.Diagnosis;
import com.proyecto.volticfit.entity.MedicalRestriction;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.DiagnosisService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller for diagnosis and medical restriction management.
 * All endpoints are ADMIN only.
 */
@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
@Tag(name = "Diagnosis", description = "Diagnosis and medical restriction endpoints")
public class DiagnosisController {

    // Inyectamos el servicio de Diagnosis para manejar la lógica de negocio relacionada con los diagnósticos y restricciones médicas
    private final DiagnosisService diagnosisService;

    @Operation(summary = "Create a diagnosis for a user - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "201", description = "Diagnosis created successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error creating diagnosis")
        }
    )
    @PostMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> createDiagnosis(@Valid @RequestBody CreateDiagnosisDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(diagnosisService.createDiagnosis(request));
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Get all diagnoses for a user",
        responses = {
            @ApiResponse(responseCode = "200", description = "Diagnoses retrieved successfully")
        }
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getDiagnosesByUser(@PathVariable Long userId) {
        try {
            List<Diagnosis> diagnoses = diagnosisService.getDiagnosesByUser(userId);
            if (diagnoses.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No diagnoses found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(diagnoses);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Get diagnosis by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDiagnosisById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(diagnosisService.getDiagnosisById(id));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Operation(summary = "Delete a diagnosis - ADMIN only")
    @DeleteMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> deleteDiagnosis(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(diagnosisService.deleteDiagnosis(id));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Operation(summary = "Create a medical restriction for a diagnosis - ADMIN only")
    @PostMapping("/restrictions")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> createRestriction(
            @Valid @RequestBody CreateMedicalRestrictionDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(diagnosisService.createRestriction(request));
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Get all restrictions for a diagnosis")
    @GetMapping("/{diagnosisId}/restrictions")
    public ResponseEntity<Object> getRestrictions(@PathVariable Long diagnosisId) {
        try {
            List<MedicalRestriction> restrictions = diagnosisService.getRestrictionsByDiagnosis(diagnosisId);
            if (restrictions.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No restrictions found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(restrictions);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Update a medical restriction - ADMIN only")
    @PutMapping("/restrictions/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> updateRestriction(
            @PathVariable Long id,
            @RequestBody UpdateMedicalRestrictionDTO request) {
        try {
            return ResponseEntity.ok(diagnosisService.updateRestriction(id, request));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Operation(summary = "Delete a medical restriction - ADMIN only")
    @DeleteMapping("/restrictions/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> deleteRestriction(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(diagnosisService.deleteRestriction(id));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}