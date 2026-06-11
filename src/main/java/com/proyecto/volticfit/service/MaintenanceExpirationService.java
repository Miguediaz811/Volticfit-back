package com.proyecto.volticfit.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.entity.MachineMaintenance;
import com.proyecto.volticfit.repository.MachineMaintenanceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio que cierra automáticamente los mantenimientos
 * cuya fecha programada ya fue alcanzada.
 * Se ejecuta diariamente a las 00:05.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class MaintenanceExpirationService {

    private final MachineMaintenanceRepository maintenanceRepository;

    /**
     * Cambia a inactivo (state = false) todos los mantenimientos
     * que estén activos y cuya fecha sea hoy o anterior.
     * Cron: todos los días a las 00:05.
     */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void closeExpiredMaintenances() {
        LocalDate today = LocalDate.now();

        List<MachineMaintenance> toClose = maintenanceRepository.findAll()
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getState())
                        && m.getDate() != null
                        && !m.getDate().isAfter(today))
                .toList();

        if (toClose.isEmpty()) {
            log.info("No hay mantenimientos por cerrar hoy ({})", today);
            return;
        }

        toClose.forEach(m -> {
            m.setState(false);
            maintenanceRepository.save(m);
            log.info("Mantenimiento id={} cerrado automáticamente (fecha: {})",
                    m.getIdMaintenance(), m.getDate());
        });

        log.info("Se cerraron {} mantenimientos expirados", toClose.size());
    }
}