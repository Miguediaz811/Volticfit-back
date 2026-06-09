package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.Report.ReportRequestDTO;
import com.proyecto.volticfit.entity.Report;
import com.proyecto.volticfit.entity.ReportAuthor;
import com.proyecto.volticfit.entity.ReportAuthorId;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.ReportAuthorRepository;
import com.proyecto.volticfit.repository.ReportRepository;
import com.proyecto.volticfit.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class that handles the core business logic for processing, validating,
 * and compiling structural consolidated administrative reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportAuthorRepository reportAuthorRepository;
    private final UsersRepository usersRepository;

    /**
     * Genera un reporte administrativo y registra en Realiza_Reporte quién lo generó.
     *
     * @param requestDTO datos del reporte
     * @param generatorUserId ID del usuario que genera el reporte
     * @return el reporte persistido
     */
    @Transactional
    public Report generateAdministrativeReport(ReportRequestDTO requestDTO, Long generatorUserId) {
        log.info("Initiating report generation for type: {} in format: {}", requestDTO.getType(), requestDTO.getFormat());

        if (requestDTO.getStartDate() != null && requestDTO.getEndDate() != null) {
            if (requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
                throw new IllegalArgumentException("The start date cannot be after the end date.");
            }
        }

        StringBuilder rawData = new StringBuilder();
        rawData.append("--- VolticFit Consolidated Report ---\n");
        rawData.append("Report Type: ").append(requestDTO.getType()).append("\n");
        rawData.append("Generated On: ").append(LocalDate.now()).append("\n");
        rawData.append("Data scope: From ").append(requestDTO.getStartDate())
               .append(" To ").append(requestDTO.getEndDate()).append("\n");
        rawData.append("Status: Processed and Optimized.\n");

        Report report = Report.builder()
                .type(requestDTO.getType())
                .format(requestDTO.getFormat())
                .content(rawData.toString())
                .generationDate(LocalDate.now())
                .build();

        Report savedReport = reportRepository.save(report);
        log.info("Report persisted with ID: {}", savedReport.getId());

        // Registrar en Realiza_Reporte quién generó el reporte
        if (generatorUserId != null) {
            Users generator = usersRepository.findById(generatorUserId)
                    .orElseThrow(() -> new RuntimeException("Usuario generador no encontrado"));

            ReportAuthorId authorId = new ReportAuthorId();
            authorId.setUserId(generatorUserId);
            authorId.setReportId(savedReport.getId());

            ReportAuthor author = new ReportAuthor();
            author.setId(authorId);
            author.setUser(generator);
            author.setReport(savedReport);
            reportAuthorRepository.save(author);

            log.info("Report author registered: userId={} for reportId={}", generatorUserId, savedReport.getId());
        }

        return savedReport;
    }

    /**
     * Devuelve todos los reportes generados por un usuario específico.
     */
    public List<Report> getReportsByUser(Long userId) {
        return reportAuthorRepository.findByUserIdUser(userId).stream()
                .map(ReportAuthor::getReport)
                .toList();
    }

    /**
     * Devuelve todos los reportes (uso admin).
     */
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
}
