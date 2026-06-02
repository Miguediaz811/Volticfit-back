package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MachineMaintenance.CreateMaintenanceDTO;
import com.proyecto.volticfit.dto.MachineMaintenance.MaintenanceResponseDTO;
import com.proyecto.volticfit.entity.MachineMaintenance;
import com.proyecto.volticfit.service.MachineMaintenanceService;

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
    public List<MachineMaintenance>
    getMachineHistory(
            @PathVariable Long idMachine) {

        return maintenanceService
                .getMachineMaintenanceHistory(
                        idMachine);
    }
}