package com.proyecto.volticfit.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.volticfit.dto.ReservationResponseDTO;
import com.proyecto.volticfit.service.ReservationService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador para consulta de horarios.
 */
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Obtiene horarios disponibles.
     */
    @GetMapping("/available")
    public ResponseEntity<Page<ReservationResponseDTO>> getSchedules(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        String token = authHeader.replace("Bearer ", "");

        return ResponseEntity.ok(
                reservationService.getSchedules(token, page, size)
        );
    }
}