package com.proyecto.volticfit.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.ExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Export", description = "Endpoints for comprehensive gym data export")
public class ExportController {

    private final ExportService exportService;

    /**
     * HU49: Crear endpoint de exportación e Implementar generación de PDF
     * Validar permisos de exportación (ADMIN only)
     * Permite valores en targetData como: general, users, sanctions, machines, maintenance, attendance
     */
    @Operation(summary = "Export gym data to PDF (general or specific module) - ADMIN only")
    @GetMapping("/pdf")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Object> exportToPdf(@RequestParam String targetData) {
        try {
            byte[] pdfBytes = exportService.generatePdfReport(targetData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "volticfit_" + targetData + "_report.pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error exporting data to PDF: {}", e.getMessage());
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage("Error generating PDF report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * HU49: Crear endpoint de exportación e Implementar generación de Excel/CSV
     * Validar permisos de exportación (ADMIN only)
     */
    @Operation(summary = "Export gym data to Excel (XLSX) - ADMIN only")
    @GetMapping("/excel")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Object> exportToExcel(@RequestParam String targetData) {
        try {
            byte[] excelBytes = exportService.generateExcelReport(targetData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "volticfit_" + targetData + "_report.xlsx");
            
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error exporting data to Excel: {}", e.getMessage());
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage("Error generating Excel report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * HU49: Crear endpoint de exportación e Implementar generación de Excel/CSV
     * Validar permisos de exportación (ADMIN only)
     */
    @Operation(summary = "Export gym data to CSV - ADMIN only")
    @GetMapping("/csv")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Object> exportToCsv(@RequestParam String targetData) {
        try {
            byte[] csvBytes = exportService.generateCsvReport(targetData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "volticfit_" + targetData + "_report.csv");
            
            return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error exporting data to CSV: {}", e.getMessage());
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage("Error generating CSV report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}