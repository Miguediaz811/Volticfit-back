package com.proyecto.volticfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.volticfit.entity.Audit;

/**
 * Repositorio para la entidad Audit.
 * Provee métodos de acceso a los logs de auditoría de asistencia manual.
 */
@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    /**
     * Obtiene todos los logs de auditoría generados por un admin específico.
     */
    List<Audit> findByAdminIdUserOrderByDateTimeDesc(Long adminId);

    /**
     * Obtiene todos los logs de auditoría sobre un usuario afectado.
     */
    List<Audit> findByAffectedUserIdUserOrderByDateTimeDesc(Long userId);
}