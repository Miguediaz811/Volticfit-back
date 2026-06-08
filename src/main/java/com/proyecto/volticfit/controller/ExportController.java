package com.proyecto.volticfit.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.ExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller for exporting system reports.
 * Only accessible by ADMIN.
 */
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Export", description = "Export system data to PDF, Excel or CSV")
public class ExportController {

    private final ExportService exportService;

    // =====================
    // PDF
    // =====================

    @Operation(summary = "Export users report as PDF - ADMIN only")
    @GetMapping("/pdf/users")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportUsersPdf() {
        return buildPdfResponse(exportService.exportUsersPdf(), "users-report.pdf");
    }

    @Operation(summary = "Export attendance report as PDF - ADMIN only")
    @GetMapping("/pdf/attendance")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportAttendancePdf() {
        return buildPdfResponse(exportService.exportAttendancePdf(), "attendance-report.pdf");
    }

    @Operation(summary = "Export machines report as PDF - ADMIN only")
    @GetMapping("/pdf/machines")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportMachinesPdf() {
        return buildPdfResponse(exportService.exportMachinesPdf(), "machines-report.pdf");
    }

    @Operation(summary = "Export sanctions report as PDF - ADMIN only")
    @GetMapping("/pdf/sanctions")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportSanctionsPdf() {
        return buildPdfResponse(exportService.exportSanctionsPdf(), "sanctions-report.pdf");
    }

    // =====================
    // EXCEL
    // =====================

    @Operation(summary = "Export users report as Excel - ADMIN only")
    @GetMapping("/excel/users")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportUsersExcel() {
        return buildExcelResponse(exportService.exportUsersExcel(), "users-report.xlsx");
    }

    @Operation(summary = "Export attendance report as Excel - ADMIN only")
    @GetMapping("/excel/attendance")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportAttendanceExcel() {
        return buildExcelResponse(exportService.exportAttendanceExcel(), "attendance-report.xlsx");
    }

    @Operation(summary = "Export machines report as Excel - ADMIN only")
    @GetMapping("/excel/machines")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportMachinesExcel() {
        return buildExcelResponse(exportService.exportMachinesExcel(), "machines-report.xlsx");
    }

    @Operation(summary = "Export sanctions report as Excel - ADMIN only")
    @GetMapping("/excel/sanctions")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportSanctionsExcel() {
        return buildExcelResponse(exportService.exportSanctionsExcel(), "sanctions-report.xlsx");
    }

    // =====================
    // CSV
    // =====================

    @Operation(summary = "Export users report as CSV - ADMIN only")
    @GetMapping("/csv/users")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportUsersCsv() {
        return buildCsvResponse(exportService.exportUsersCsv(), "users-report.csv");
    }

    @Operation(summary = "Export attendance report as CSV - ADMIN only")
    @GetMapping("/csv/attendance")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportAttendanceCsv() {
        return buildCsvResponse(exportService.exportAttendanceCsv(), "attendance-report.csv");
    }

    @Operation(summary = "Export machines report as CSV - ADMIN only")
    @GetMapping("/csv/machines")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportMachinesCsv() {
        return buildCsvResponse(exportService.exportMachinesCsv(), "machines-report.csv");
    }

    @Operation(summary = "Export sanctions report as CSV - ADMIN only")
    @GetMapping("/csv/sanctions")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportSanctionsCsv() {
        return buildCsvResponse(exportService.exportSanctionsCsv(), "sanctions-report.csv");
    }

    // =====================
    // Private helpers
    // =====================

    private ResponseEntity<byte[]> buildPdfResponse(byte[] data, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    private ResponseEntity<byte[]> buildExcelResponse(byte[] data, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    private ResponseEntity<byte[]> buildCsvResponse(byte[] data, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}