package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.Report.ReportRequestDTO;
import com.proyecto.volticfit.service.ReportService;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<?> generateReport(@RequestBody ReportRequestDTO request) {
        // Asume que tu servicio procesa esto
        // var data = reportService.generateData(request);
        return ResponseEntity.ok().build(); 
    }

    @PostMapping("/download/{format}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<byte[]> downloadReport(
            @PathVariable String format, 
            @RequestBody ReportRequestDTO request) {
        
        // byte[] file = reportService.generateFile(request, format);
        byte[] file = new byte[0]; // Reemplazar por la llamada al servicio
        
        String contentType = format.equalsIgnoreCase("pdf") ? 
                MediaType.APPLICATION_PDF_VALUE : "application/vnd.ms-excel";
                
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte." + format)
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }
}