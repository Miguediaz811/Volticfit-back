package com.proyecto.volticfit.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.volticfit.entity.IdempotencyRecord;

/**
 * Repositorio para gestionar registros de idempotencia.
 */
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {

    /**
     * Busca un registro de idempotencia por su clave única.
     *
     * @param idempotencyKey la clave UUID enviada por el cliente
     * @return el registro si existe
     */
    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

    /**
     * Elimina todos los registros creados antes de la fecha indicada.
     * Usado por el servicio de limpieza automática.
     *
     * @param cutoff fecha límite
     */
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime cutoff);
}
