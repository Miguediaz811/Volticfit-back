package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Machine.FailureRequestDTO;
import com.proyecto.volticfit.entity.EquipmentFailure;
import com.proyecto.volticfit.repository.EquipmentFailureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentFailureService {

    private final EquipmentFailureRepository repository;

    public void registerFailure(Long userId, FailureRequestDTO dto) {
        if (dto.getMaquinaId() == null || dto.getDescripcion() == null || dto.getDescripcion().isBlank()) {
            throw new RuntimeException("Faltan campos obligatorios");
        }
        
        EquipmentFailure failure = new EquipmentFailure();
        failure.setMaquinaId(dto.getMaquinaId());
        failure.setUsuarioReporteId(userId); // Tarea 1.6: Registrar al usuario que reporta
        failure.setDescripcion(dto.getDescripcion());
        repository.save(failure);
    }

    public List<EquipmentFailure> getAllFailures() {
        return repository.findAll();
    }
}