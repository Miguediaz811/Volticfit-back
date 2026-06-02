package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.proyecto.volticfit.dto.MachineMaintenance.CreateMaintenanceDTO;
import com.proyecto.volticfit.dto.MachineMaintenance.MaintenanceResponseDTO;
import com.proyecto.volticfit.entity.MachineMaintenance;
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
    @GetMapping("/machine/{machineId}")
    public List<MachineMaintenance>
    getMachineHistory(
            @PathVariable Long machineId) {

        return maintenanceService
                .getMachineMaintenanceHistory(
                        machineId);
    }
}