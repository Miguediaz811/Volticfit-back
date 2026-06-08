package com.proyecto.volticfit.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.volticfit.entity.MachineMaintenance;

public interface MachineMaintenanceRepository extends JpaRepository<MachineMaintenance, Long> {

    Optional<MachineMaintenance>
    findByMachine_IdMachineAndDate(
            Long idMachine,
            LocalDate date
    );

    List<MachineMaintenance>
    findByMachine_IdMachine(Long idMachine);
}
