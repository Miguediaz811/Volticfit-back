package com.proyecto.volticfit.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.AttendenceResponseDTO;
import com.proyecto.volticfit.service.AttendenceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attendence")
@RequiredArgsConstructor
public class AttendenceController {

    private final AttendenceService attendenceService;

    /**
     * Endpoint para consultar asistencias del usuario autenticado.
     */
    @GetMapping
    public ResponseEntity<Page<AttendenceResponseDTO>> getAttendences(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        String token = authHeader.replace("Bearer ", "");

        Page<AttendenceResponseDTO> response =
                attendenceService.getAttendences(token, page, size);

        return ResponseEntity.ok(response);
    }
}