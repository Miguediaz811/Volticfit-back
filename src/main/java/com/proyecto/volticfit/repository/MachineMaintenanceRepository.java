package com.proyecto.volticfit.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.MachineMaintenance;

public interface MachineMaintenanceRepository
        extends JpaRepository<MachineMaintenance, Long> {

    Optional<MachineMaintenance>
    findByMachineIdAndMaintenanceDateAndMaintenanceTime(
            Long machineId,
            LocalDate maintenanceDate,
            LocalTime maintenanceTime
    );

    List<MachineMaintenance>
    findByMachineId(Long machineId);
}