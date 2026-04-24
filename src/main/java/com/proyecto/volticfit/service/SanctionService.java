package com.proyecto.volticfit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.entity.Sanction;
import com.proyecto.volticfit.repository.SanctionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SanctionService {
    
    private final SanctionRepository sanctionRepository;

    public List<Sanction> getAll() {
        return sanctionRepository.findAll();
    }

    public Sanction getById(Long id) {
        return sanctionRepository.findById(id).orElseThrow(() -> new RuntimeException("Sanción no encontrada"));
    }

    public List<Sanction> getByUser(Long userId) {
        return sanctionRepository.findByUsuarioSanctionListUserId(userId);
    }
}
