package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.Report.ReportRequestDTO;
import com.proyecto.volticfit.entity.Report;
import com.proyecto.volticfit.service.ReportService;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.enums.RoleEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Genera un reporte y registra en Realiza_Reporte al usuario que lo generó.
     */
    @PostMapping("/generate")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Report> generateReport(
            @RequestBody ReportRequestDTO request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Report report = reportService.generateAdministrativeReport(request, userId);
        return ResponseEntity.ok(report);
    }

    /**
     * Descarga el reporte en el formato indicado.
     */
    @PostMapping("/download/{format}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> downloadReport(
            @PathVariable String format,
            @RequestBody ReportRequestDTO request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Report report = reportService.generateAdministrativeReport(request, userId);

        // El contenido se retorna como bytes del texto del reporte
        byte[] file = report.getContent() != null ? report.getContent().getBytes() : new byte[0];

        String contentType = format.equalsIgnoreCase("pdf") ?
                MediaType.APPLICATION_PDF_VALUE : "application/vnd.ms-excel";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte." + format)
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

    /**
     * Devuelve todos los reportes (historial admin).
     */
    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    /**
     * Devuelve los reportes generados por el usuario autenticado.
     */
    @GetMapping("/my")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<List<Report>> getMyReports(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return ResponseEntity.ok(reportService.getReportsByUser(userId));
    }
}
