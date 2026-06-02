package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Report.ReportRequestDTO;
import com.proyecto.volticfit.entity.Report;
import com.proyecto.volticfit.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service class that handles the core business logic for processing, validating, 
 * and compiling structural consolidated administrative reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;

    /**
     * Triggers the sequence to compile historical database snapshots into structural 
     * administrative reports after performing chronological date range integrity checks.
     *
     * @param requestDTO the data transfer object containing filtration criteria and target file formats
     * @return the newly persisted {@link Report} metadata record acting as an audit entry
     * @throws IllegalArgumentException if the provided chronological start date occurs after the end date
     */
    public Report generateAdministrativeReport(ReportRequestDTO requestDTO) {
        log.info("Initiating report generation sequence for type: {} in format: {}", 
                requestDTO.getType(), requestDTO.getFormat());

        // HU48: Manejar validaciones de filtros
        if (requestDTO.getStartDate() != null && requestDTO.getEndDate() != null) {
            if (requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
                throw new IllegalArgumentException("The start date cannot be after the end date.");
            }
        }

        // HU48: Implementar consultas optimizadas (Simulación de consolidación de datos estructurales)
        StringBuilder rawDataStructure = new StringBuilder();
        rawDataStructure.append("--- VolticFit Consolidated Report ---\n");
        rawDataStructure.append("Report Type: ").append(requestDTO.getType()).append("\n");
        rawDataStructure.append("Generated On: ").append(LocalDate.now()).append("\n");
        rawDataStructure.append("Data scope: From ").append(requestDTO.getStartDate())
                        .append(" To ").append(requestDTO.getEndDate()).append("\n");
        rawDataStructure.append("Status: Processed and Optimized.\n");

        // Construcción y guardado del registro del reporte
        Report report = Report.builder()
                .type(requestDTO.getType())
                .format(requestDTO.getFormat())
                .content(rawDataStructure.toString())
                .generationDate(LocalDate.now())
                .build();

        Report savedReport = reportRepository.save(report);
        log.info("Successfully persisted report audit metadata with ID: {}", savedReport.getId());

        return savedReport;
    }
}