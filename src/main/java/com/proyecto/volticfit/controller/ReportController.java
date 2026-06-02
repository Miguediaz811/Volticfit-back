package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.Report.ReportRequestDTO;
import com.proyecto.volticfit.entity.Report;
import com.proyecto.volticfit.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing administrative, financial, and operational gym reports.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    /**
     * Triggers the administrative engine to generate and persist a new statistical report.
     * Includes error handling and structural validation blocks.
     *
     * @param request the request body details containing dates, scopes, and parameters for processing
     * @return a ResponseEntity containing the generated report entity or an error message description
     */
    @PostMapping("/generate")
    public ResponseEntity<?> createReport(@RequestBody ReportRequestDTO request) {
        try {
            // HU48: Implementar control de acceso y manejo de errores
            Report generatedReport = reportService.generateAdministrativeReport(request);
            return ResponseEntity.ok(generatedReport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred during report preparation.");
        }
    }
}