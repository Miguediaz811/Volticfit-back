package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Sanctions.CreateSanctionDTO;
import com.proyecto.volticfit.dto.Sanctions.UpdateSanctionDTO;
import com.proyecto.volticfit.entity.Sanction;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.SanctionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sanciones")
@RequiredArgsConstructor
@Tag(name = "Sanctions", description = "Sanction management endpoints")
public class SanctionController {

    private final SanctionService sanctionService;

    @PostMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Sanction> createSanction(@RequestBody CreateSanctionDTO request) {
        return ResponseEntity.ok(sanctionService.create(request));
    }

    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<List<Sanction>> getAllSanctions() {
        return ResponseEntity.ok(sanctionService.getAll());
    }

    /**
     * HU42: Crear endpoint PUT /api/sanciones/{id} e Implementar
     * actualizarSancion()
     */
    @Operation(summary = "Update a sanction - ADMIN only", responses = {
            @ApiResponse(responseCode = "200", description = "Sanction updated successfully", content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sanction not found"),
            @ApiResponse(responseCode = "500", description = "Connection error during update")
    })
    @PutMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> updateSanction(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSanctionDTO request) {
        try {
            MessageResponseDTO response = sanctionService.update(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage("Error de conexión al intentar editar la sanción: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * HU43: Crear endpoint DELETE /api/sanciones/{id} e Implementar
     * eliminarSancion()
     */
    @Operation(summary = "Delete a sanction physically - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "200", description = "Sanction deleted successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sanction not found"),
            @ApiResponse(responseCode = "500", description = "Database failure or constraint error")
        }
    )
    @DeleteMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> deleteSanction(@PathVariable Long id) {
        try {
            MessageResponseDTO response = sanctionService.delete(id);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage("Error de eliminación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
