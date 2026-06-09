package com.proyecto.volticfit.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.entity.MedicalRestriction;
import com.proyecto.volticfit.repository.MedicalRestrictionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service for automatically expiring medical restrictions.
 * Runs daily at midnight.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class RestrictionExpirationService {

    private final MedicalRestrictionRepository restrictionRepository;

    /**
     * Automatically deactivates expired medical restrictions.
     * Runs every day at 00:00.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireRestrictions() {
        LocalDate today = LocalDate.now();

        // Find all active restrictions that have expired
        List<MedicalRestriction> allActive = restrictionRepository.findAll()
                .stream()
                .filter(r -> Boolean.TRUE.equals(r.getState())
                        && r.getEndDate() != null
                        && r.getEndDate().isBefore(today))
                .toList();

        if (allActive.isEmpty()) {
            log.info("No expired restrictions found");
            return;
        }

        allActive.forEach(r -> {
            r.setState(false);
            restrictionRepository.save(r);
            log.info("Restriction {} expired and deactivated", r.getIdRestriction());
        });

        log.info("Expired {} medical restrictions", allActive.size());
    }
}