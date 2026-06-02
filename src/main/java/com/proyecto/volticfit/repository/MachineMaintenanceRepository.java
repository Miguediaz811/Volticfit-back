package com.proyecto.volticfit.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.MachineMaintenance;

public interface MachineMaintenanceRepository extends JpaRepository<MachineMaintenance, Long> {

    Optional<MachineMaintenance>
    findByMachine_IdMachineAndDateAndTime(
            Long idMachine,
            LocalDate date,
            LocalTime time
    );

    List<MachineMaintenance>
    findByMachine_IdMachine(Long idMachine);
}