package com.proyecto.volticfit.service;

import java.time.LocalDate;
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
                response.setMessage("Selecciona un equipo para programar el mantenimiento");
                return response;
            }

            if (request.getDate() == null) {

                response.setStatus("ERROR");
                response.setMessage("La fecha del mantenimiento es obligatoria");
                return response;
            }

            Machine machine =
                    machineRepository.findById(
                            request.getMachineId())
                            .orElse(null);

            if (machine == null) {

                response.setStatus("ERROR");
                response.setMessage("No se encontro el equipo seleccionado");
                return response;
            }

            boolean exists =
                    maintenanceRepository
                            .findByMachine_IdMachineAndDate(
                                    request.getMachineId(),
                                    request.getDate())
                            .isPresent();

            if (exists) {

                response.setStatus("ERROR");
                response.setMessage(
                        "Ya existe un mantenimiento programado para ese equipo en esa fecha");

                return response;
            }

            MachineMaintenance maintenance =
                    new MachineMaintenance();

            maintenance.setMachine(machine);
            maintenance.setDate(
                    request.getDate());
            maintenance.setType(
                    request.getType());
            maintenance.setDescription(
                    request.getDescription());
            maintenance.setResponsible(
                    request.getResponsible());
            maintenance.setState(request.getDate().isAfter(LocalDate.now()));

            maintenanceRepository.save(
                    maintenance);

            response.setStatus("SUCCESS");
            response.setMessage(
                    "Mantenimiento programado correctamente");

            log.info(
                    "Maintenance scheduled for machine {}",
                    machine.getIdMachine());

        } catch (Exception e) {

            log.error(
                    "Error scheduling maintenance: {}",
                    e.getMessage());

            response.setStatus("ERROR");
            response.setMessage(
                    "No se pudo programar el mantenimiento. Verifica la informacion e intenta nuevamente.");
        }

        return response;
    }

    /**
     * Obtiene el historial de mantenimientos
     * de una máquina.
     *
     * @param idMachine identificador de máquina
     * @return historial encontrado
     */
    public List<MachineMaintenance>
    getMachineMaintenanceHistory(
            Long idMachine) {

        return refreshExpiredMaintenance(
                maintenanceRepository.findByMachine_IdMachine(idMachine));
    }

    public List<MachineMaintenance> getAllMaintenanceHistory(String requesterRole) {
        if (!"admin".equalsIgnoreCase(requesterRole)) {
            throw new RuntimeException("No tienes permiso para consultar mantenimientos");
        }
        return refreshExpiredMaintenance(maintenanceRepository.findAll());
    }

    /**
     * Obtiene todos los mantenimientos filtrados por rango de fechas.
     */
    public List<MachineMaintenance> getAllMaintenanceFiltered(String requesterRole, LocalDate startDate, LocalDate endDate) {
        if (!"admin".equalsIgnoreCase(requesterRole)) {
            throw new RuntimeException("No tienes permiso para consultar mantenimientos");
        }
        return refreshExpiredMaintenance(maintenanceRepository.findAll()).stream()
                .filter(m -> {
                    // Excluir registros sin máquina o con nombre "sin maquina"
                    if (m.getMachine() == null) return false;
                    String machineName = m.getMachine().getName();
                    if (machineName == null || machineName.trim().equalsIgnoreCase("sin maquina")) return false;
                    // Filtro por fecha
                    if (startDate == null && endDate == null) return true;
                    if (m.getDate() == null) return false;
                    if (startDate != null && m.getDate().isBefore(startDate)) return false;
                    if (endDate != null && m.getDate().isAfter(endDate)) return false;
                    return true;
                })
                .sorted((a, b) -> {
                    if (a.getDate() == null) return 1;
                    if (b.getDate() == null) return -1;
                    return b.getDate().compareTo(a.getDate());
                })
                .toList();
    }

    @Transactional
    protected List<MachineMaintenance> refreshExpiredMaintenance(List<MachineMaintenance> records) {
        LocalDate today = LocalDate.now();
        records.stream()
                .filter(m -> Boolean.TRUE.equals(m.getState()))
                .filter(m -> m.getDate() != null && !m.getDate().isAfter(today))
                .forEach(m -> {
                    m.setState(false);
                    maintenanceRepository.save(m);
                });
        return records;
    }
}
