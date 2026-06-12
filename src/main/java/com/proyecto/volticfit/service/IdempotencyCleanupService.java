package com.proyecto.volticfit.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.repository.IdempotencyRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio programado que limpia automáticamente los registros de idempotencia
 * expirados para evitar el crecimiento indefinido de la tabla.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class IdempotencyCleanupService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;

    /**
     * Ejecuta la limpieza cada 24 horas (86400000 ms).
     * Elimina todos los registros con más de 24 horas de antigüedad.
     */
    @Scheduled(fixedRate = 86400000)
    public void cleanupExpiredRecords() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        idempotencyRecordRepository.deleteByCreatedAtBefore(cutoff);
        log.info("🧹 Limpieza de registros de idempotencia completada (anteriores a {})", cutoff);
    }
}
