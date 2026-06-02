package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.Machine.MachineStatusDTO;
import com.proyecto.volticfit.service.MachineStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for monitoring gym machinery infrastructure and operational status.
 */
@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MachineStatusController {

    private final MachineStatusService machineStatusService;

    /**
     * Retrieves the complete inventory list of gym machines along with their operational states.
     *
     * @return a ResponseEntity containing a list of all machine status records
     */
    @GetMapping("/status")
    public ResponseEntity<List<MachineStatusDTO>> getAllMachinesStatus() {
        List<MachineStatusDTO> machines = machineStatusService.getAllMachinesStatus();
        return ResponseEntity.ok(machines);
    }

    /**
     * Retrieves a filtered list of gym machines based on their type and operational status.
     *
     * @param type   the category or type of machine to filter by
     * @param status the operational availability state (true for active, false for out of service)
     * @return a ResponseEntity containing the filtered list of machine status records
     */
    @GetMapping("/status/filter")
    public ResponseEntity<List<MachineStatusDTO>> getMachinesByFilter(
            @RequestParam String type,
            @RequestParam Boolean status) {
        List<MachineStatusDTO> filteredMachines = machineStatusService.getMachinesByTypeAndStatus(type, status);
        return ResponseEntity.ok(filteredMachines);
    }
}