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
import com.proyecto.volticfit.dto.Machine.MachineRequestDTO;
import com.proyecto.volticfit.dto.Machine.MachineResponseDTO;
import com.proyecto.volticfit.dto.Machine.UpdateMachineDTO; // HU37: Cambiado a nombre en inglés
import com.proyecto.volticfit.entity.Machine;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.MachineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador encargado
 * de la gestión de máquinas.
 */
@RestController
@RequestMapping("/api/maquinas")
@RequiredArgsConstructor
@Tag(name = "Machines", description = "Machine management endpoints")
public class MachineController {

    /**
     * Servicio principal.
     */
    private final MachineService machineService;

    /**
     * Registra una máquina.
     *
     * @param request datos máquina
     * @return resultado operación
     */
    @PostMapping
    public MachineResponseDTO registrar(
            @RequestBody MachineRequestDTO request) {
        return machineService.registrarMaquina(request);
    }

    /**
     * Consulta todas las máquinas.
     * HU38: Crear endpoint GET /api/maquinas e Implementar manejo de errores de conexión
     *
     * @return listado de máquinas
     */
    @Operation(summary = "Get all active machines",
        responses = {
            @ApiResponse(responseCode = "200", description = "Machines retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Connection or database error")
        }
    )
    @GetMapping
    public ResponseEntity<Object> getAllMachines() {
        try {
            List<Machine> machines = machineService.getAllActiveMachines();
            return ResponseEntity.ok(machines);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage("Error de conexión al consultar el inventario de máquinas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * HU37: Crear endpoint PUT /api/maquinas/{id}
     */
    @Operation(summary = "Update an existing machine - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "200", description = "Machine updated successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Machine not found")
        }
    )
    @PutMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> updateMachine(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMachineDTO request) {
        try {
            MessageResponseDTO response = machineService.updateMachine(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * HU36: Crear endpoint DELETE /api/maquinas/{id}
     */
    @Operation(summary = "Logically delete a machine - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "200", description = "Machine logical delete completed successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Active dependencies found conflict"),
            @ApiResponse(responseCode = "404", description = "Machine not found")
        }
    )
    @DeleteMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> deleteMachine(@PathVariable Long id) {
        try {
            MessageResponseDTO response = machineService.deleteMachine(id);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}