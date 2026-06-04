package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findByUsuarioId(Long usuarioId, Pageable pageable);
}