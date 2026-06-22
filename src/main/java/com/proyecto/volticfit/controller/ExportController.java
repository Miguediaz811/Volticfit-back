package com.proyecto.volticfit.controller;

import java.time.LocalDate;
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
    public ResponseEntity<byte[]> exportAttendancePdf(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDate start = startDate != null && !startDate.isEmpty() ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null && !endDate.isEmpty() ? LocalDate.parse(endDate) : null;
        return buildPdfResponse(exportService.exportAttendancePdf(userId, start, end), "attendance-report.pdf");
    }

    @Operation(summary = "Export machines report as PDF - ADMIN only")
    @GetMapping("/pdf/machines")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportMachinesPdf(
            @RequestParam(required = false) String status) {
        return buildPdfResponse(exportService.exportMachinesPdf(status), "machines-report.pdf");
    }

    @Operation(summary = "Export sanctions report as PDF - ADMIN only")
    @GetMapping("/pdf/sanctions")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportSanctionsPdf(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status) {
        LocalDate start = startDate != null && !startDate.isEmpty() ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null && !endDate.isEmpty() ? LocalDate.parse(endDate) : null;
        return buildPdfResponse(exportService.exportSanctionsPdf(userId, start, end, status), "sanctions-report.pdf");
    }

    @Operation(summary = "Export maintenance report as PDF - ADMIN only")
    @GetMapping("/pdf/maintenance")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportMaintenancePdf(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDate start = startDate != null && !startDate.isEmpty() ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null && !endDate.isEmpty() ? LocalDate.parse(endDate) : null;
        return buildPdfResponse(exportService.exportMaintenancePdf(start, end), "maintenance-report.pdf");
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
    public ResponseEntity<byte[]> exportAttendanceExcel(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDate start = startDate != null && !startDate.isEmpty() ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null && !endDate.isEmpty() ? LocalDate.parse(endDate) : null;
        return buildExcelResponse(exportService.exportAttendanceExcel(userId, start, end), "attendance-report.xlsx");
    }

    @Operation(summary = "Export machines report as Excel - ADMIN only")
    @GetMapping("/excel/machines")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportMachinesExcel(
            @RequestParam(required = false) String status) {
        return buildExcelResponse(exportService.exportMachinesExcel(status), "machines-report.xlsx");
    }

    @Operation(summary = "Export sanctions report as Excel - ADMIN only")
    @GetMapping("/excel/sanctions")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportSanctionsExcel(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status) {
        LocalDate start = startDate != null && !startDate.isEmpty() ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null && !endDate.isEmpty() ? LocalDate.parse(endDate) : null;
        return buildExcelResponse(exportService.exportSanctionsExcel(userId, start, end, status), "sanctions-report.xlsx");
    }

    @Operation(summary = "Export maintenance report as Excel - ADMIN only")
    @GetMapping("/excel/maintenance")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportMaintenanceExcel(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDate start = startDate != null && !startDate.isEmpty() ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null && !endDate.isEmpty() ? LocalDate.parse(endDate) : null;
        return buildExcelResponse(exportService.exportMaintenanceExcel(start, end), "maintenance-report.xlsx");
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

    @Operation(summary = "Export maintenance report as CSV - ADMIN only")
    @GetMapping("/csv/maintenance")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> exportMaintenanceCsv() {
        return buildCsvResponse(exportService.exportMaintenanceCsv(), "maintenance-report.csv");
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
