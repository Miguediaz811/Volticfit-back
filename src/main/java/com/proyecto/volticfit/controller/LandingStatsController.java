package com.proyecto.volticfit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.Landing.LandingStatsDTO;
import com.proyecto.volticfit.service.LandingStatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class LandingStatsController {

    private final LandingStatsService landingStatsService;

    @GetMapping("/landing-stats")
    public LandingStatsDTO getLandingStats() {
        return landingStatsService.getStats();
    }
}
