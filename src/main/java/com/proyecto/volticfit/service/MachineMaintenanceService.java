package com.proyecto.volticfit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MachineMaintenance.CreateMaintenanceDTO;
import com.proyecto.volticfit.dto.MachineMaintenance.MaintenanceResponseDTO;
import com.proyecto.volticfit.entity.Machine;
import com.proyecto.volticfit.entity.MachineMaintenance;
import com.proyecto.volticfit.repository.MachineMaintenanceRepository;
import com.proyecto.volticfit.repository.MachineRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio encargado de gestionar
 * los mantenimientos de máquinas.
 *
 * @author Miguel
 * @version 1.0
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class MachineMaintenanceService {

    private final MachineRepository machineRepository;

    private final MachineMaintenanceRepository maintenanceRepository;

    /**
     * Programa un mantenimiento preventivo.
     *
     * @param request información recibida
     * @return resultado de la operación
     */
    @Transactional
    public MaintenanceResponseDTO scheduleMaintenance(
            CreateMaintenanceDTO request) {

        MaintenanceResponseDTO response =
                new MaintenanceResponseDTO();

        try {

            if (request.getMachineId() == null) {

                response.setStatus("ERROR");
                response.setMessage("Machine is required");
                return response;
            }

            if (request.getDate() == null) {

                response.setStatus("ERROR");
                response.setMessage("Maintenance date is required");
                return response;
            }

            if (request.getTime() == null) {

                response.setStatus("ERROR");
                response.setMessage("Maintenance time is required");
                return response;
            }

            Machine machine =
                    machineRepository.findById(
                            request.getMachineId())
                            .orElse(null);

            if (machine == null) {

                response.setStatus("ERROR");
                response.setMessage("Machine not found");
                return response;
            }

            boolean exists =
                    maintenanceRepository
                            .findByMachineIdAndMaintenanceDateAndMaintenanceTime(
                                    request.getMachineId(),
                                    request.getDate(),
                                    request.getTime())
                            .isPresent();

            if (exists) {

                response.setStatus("ERROR");
                response.setMessage(
                        "There is already a maintenance scheduled at that date and time");

                return response;
            }

            MachineMaintenance maintenance =
                    new MachineMaintenance();

            maintenance.setMachine(machine);
            maintenance.setDate(
                    request.getDate());
            maintenance.setTime(
                    request.getTime());
            maintenance.setDescription(
                    request.getDescription());
            maintenance.setState(true);

            maintenanceRepository.save(
                    maintenance);

            response.setStatus("SUCCESS");
            response.setMessage(
                    "Maintenance scheduled successfully");

            log.info(
                    "Maintenance scheduled for machine {}",
                    machine.getIdMachine());

        } catch (Exception e) {

            log.error(
                    "Error scheduling maintenance: {}",
                    e.getMessage());

            response.setStatus("ERROR");
            response.setMessage(
                    "Internal server error");
        }

        return response;
    }

    /**
     * Obtiene el historial de mantenimientos
     * de una máquina.
     *
     * @param machineId identificador de máquina
     * @return historial encontrado
     */
    public List<MachineMaintenance>
    getMachineMaintenanceHistory(
            Long machineId) {

        return maintenanceRepository
                .findByMachineId(machineId);
    }
}