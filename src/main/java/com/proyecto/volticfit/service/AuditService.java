package com.proyecto.volticfit.service;

import com.proyecto.volticfit.entity.ActivityLog;
import com.proyecto.volticfit.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ActivityLogRepository repository;

    @Async
    public void saveLogAsync(Long userId, String method, String uri, int status) {
        ActivityLog log = new ActivityLog();
        log.setUsuarioId(userId);
        log.setAccion(method);
        log.setModulo(uri);
        log.setStatusHttp(status);
        repository.save(log);
    }

    public Page<ActivityLog> obtenerHistorial(Long usuarioId, Pageable pageable) {
        if (usuarioId != null) {
            return repository.findByUsuarioId(usuarioId, pageable);
        }
        return repository.findAll(pageable);
    }
}