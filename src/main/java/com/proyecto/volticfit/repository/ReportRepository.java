package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing data operations, persistence, and audit logs of {@link Report} entities.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    // Repositorio base para la persistencia y auditoría de reportes administrativos
}