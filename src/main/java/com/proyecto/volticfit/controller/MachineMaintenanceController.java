package com.proyecto.volticfit.controller;

import java.time.LocalDate;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MachineMaintenance.CreateMaintenanceDTO;
import com.proyecto.volticfit.dto.MachineMaintenance.MaintenanceResponseDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.service.MachineMaintenanceService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Controlador encargado de la gestión
 * de mantenimientos.
 *
 * @author Miguel
 * @version 1.0
 */
@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MachineMaintenanceController {

    private final MachineMaintenanceService maintenanceService;

    /**
     * Programa un mantenimiento.
     */
    @PostMapping
    public MaintenanceResponseDTO scheduleMaintenance(
            @RequestBody CreateMaintenanceDTO request) {

        return maintenanceService
                .scheduleMaintenance(request);
    }

    /**
     * Consulta historial por máquina.
     */
    @GetMapping("/machine/{idMachine}")
    public ResponseEntity<Object>
    getMachineHistory(
            @PathVariable Long idMachine) {
        try {
            return ResponseEntity.ok(maintenanceService
                    .getMachineMaintenanceHistory(
                            idMachine));
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage("No se pudo cargar el historial de mantenimiento. Verifica que la base de datos tenga las columnas requeridas para mantenimiento.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/report")
    public ResponseEntity<Object> getMaintenanceReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest httpRequest) {
        try {
            String role = (String) httpRequest.getAttribute("role");
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end   = endDate   != null ? LocalDate.parse(endDate)   : null;
            return ResponseEntity.ok(maintenanceService.getAllMaintenanceFiltered(role, start, end));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllHistory(HttpServletRequest httpRequest) {
        try {
            String role = (String) httpRequest.getAttribute("role");
            return ResponseEntity.ok(maintenanceService.getAllMaintenanceHistory(role));
        } catch (DataAccessException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage("No se pudo cargar el historial de mantenimiento. Verifica que la base de datos tenga las columnas requeridas para mantenimiento.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }
}