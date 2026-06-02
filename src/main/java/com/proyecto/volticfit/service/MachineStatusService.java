package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Machine.MachineStatusDTO;
import com.proyecto.volticfit.entity.Machine;
import com.proyecto.volticfit.repository.MachineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class that handles the core business logic for processing, filtering, 
 * and retrieving structural status metrics across gym machinery inventory.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MachineStatusService {

    private final MachineRepository machineRepository;

    /**
     * Retrieves all machine records from the database infrastructure layers and maps 
     * them into unified data transfer objects.
     *
     * @return a list of {@link MachineStatusDTO} representing the full inventory status
     */
    public List<MachineStatusDTO> getAllMachinesStatus() {
        log.info("Fetching operational status for all inventory machines");
        return machineRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filters the gym machinery infrastructure layer using criteria parameters for machine category 
     * and current operational state.
     *
     * @param type   the category description string to filter by (e.g., "Cardio")
     * @param status the operational availability boolean flag (true for active, false for out of service)
     * @return a filtered list of {@link MachineStatusDTO} matching the query parameters
     */
    public List<MachineStatusDTO> getMachinesByTypeAndStatus(String type, Boolean status) {
        log.info("Filtering machine inventory by type: {} and status: {}", type, status);
        return machineRepository.findByTypeAndState(type, status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Transforms an internal structural {@link Machine} database entity record into an external 
     * data transfer object layer wrapper.
     *
     * @param machine the internal source entity instance to extract values from
     * @return a mapped and configured {@link MachineStatusDTO} instance
     */
    private MachineStatusDTO mapToDTO(Machine machine) {
        return MachineStatusDTO.builder()
                .id(machine.getIdMachine())
                .name(machine.getName())
                .type(machine.getType())
                .registrationDate(machine.getRegistrationDate())
                .status(machine.getState())
                .build();
    }
}