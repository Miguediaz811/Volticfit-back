package com.proyecto.volticfit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.entity.Sanction;
import com.proyecto.volticfit.repository.SanctionRepository;
import com.proyecto.volticfit.repository.UserSanctionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SanctionService {
    
    private final SanctionRepository sanctionRepository;

    private final UserSanctionRepository userSanctionRepository;

    public List<Sanction> getAll() {
        return sanctionRepository.findAll();
    }

    public Sanction getById(Long id) {
        return sanctionRepository.findById(id).orElseThrow(() -> new RuntimeException("Sanción no encontrada"));
    }

    public List<Sanction> getByUser(Long userId) {
        if (userId == null) {
            throw new RuntimeException("User ID must not be null");
        }
        List<Sanction> sanctions = userSanctionRepository.findByUserIdUser(userId)
                .stream()
                .map(us -> us.getSanction())
                .toList();
 
        if (sanctions.isEmpty()) {
            return List.of();
        }
        return sanctions;
    }
}
