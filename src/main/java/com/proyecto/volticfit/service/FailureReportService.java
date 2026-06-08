package com.proyecto.volticfit.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.Failures.CreateFailureReportDTO;
import com.proyecto.volticfit.dto.Failures.FailureReportDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.entity.EquipmentFailure;
import com.proyecto.volticfit.entity.Machine;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.EquipmentFailureRepository;
import com.proyecto.volticfit.repository.MachineRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FailureReportService {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CO"));

    private final MachineRepository machineRepository;
    private final UsersRepository usersRepository;
    private final EquipmentFailureRepository failureRepository;

    public FailureReportDTO create(CreateFailureReportDTO request, Long userId) {
        Machine machine = machineRepository.findById(request.getMachineId())
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDateTime now = LocalDateTime.now();

        EquipmentFailure failure = new EquipmentFailure();
        failure.setMaquinaId(machine.getIdMachine());
        failure.setUsuarioReporteId(user.getIdUser());
        failure.setDescripcion(request.getDescription());
        failure.setPrioridad(normalizePriority(request.getPriority()));
        failure.setEstado("Reportada");
        failure.setFechaReporte(now);
        EquipmentFailure saved = failureRepository.save(failure);

        return toDto(saved, machine, user);
    }

    public List<FailureReportDTO> getAll(String role) {
        requireAdmin(role);
        return failureRepository.findAll().stream()
                .map(this::toDto)
                .sorted(Comparator.comparing(FailureReportDTO::getCreatedAt).reversed())
                .toList();
    }

    public MessageResponseDTO updateStatus(String code, String status, String role) {
        requireAdmin(role);
        EquipmentFailure report = failureRepository.findById(extractId(code))
                .orElseThrow(() -> new RuntimeException("Reporte de falla no encontrado"));
        report.setEstado(normalizeStatus(status));
        failureRepository.save(report);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Estado de falla actualizado");
        return response;
    }

    private void requireAdmin(String role) {
        if (!"admin".equalsIgnoreCase(role)) {
            throw new RuntimeException("No tienes permiso para consultar fallas reportadas");
        }
    }

    private String normalizePriority(String priority) {
        String value = priority == null ? "" : priority.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "alta" -> "Alta";
            case "media" -> "Media";
            case "baja" -> "Baja";
            default -> "Media";
        };
    }

    private String normalizeStatus(String status) {
        String value = status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "reportada" -> "Reportada";
            case "en revision", "en revisión", "revision", "revisión" -> "En revision";
            case "resuelta", "resuelto" -> "Resuelta";
            default -> throw new RuntimeException("Estado de falla invalido");
        };
    }

    private FailureReportDTO toDto(EquipmentFailure failure) {
        Machine machine = failure.getMaquinaId() == null
                ? null
                : machineRepository.findById(failure.getMaquinaId()).orElse(null);
        Users user = failure.getUsuarioReporteId() == null
                ? null
                : usersRepository.findById(failure.getUsuarioReporteId()).orElse(null);
        return toDto(failure, machine, user);
    }

    private FailureReportDTO toDto(EquipmentFailure failure, Machine machine, Users user) {
        String userName = user == null ? "-" : ((user.getNames() == null ? "" : user.getNames()) + " "
                + (user.getSurnames() == null ? "" : user.getSurnames())).trim();
        return new FailureReportDTO(
                "FAL-" + failure.getId(),
                failure.getMaquinaId(),
                machine == null ? "Equipo no encontrado" : machine.getName(),
                failure.getUsuarioReporteId(),
                userName.isBlank() && user != null ? user.getEmail() : userName,
                failure.getDescripcion(),
                failure.getPrioridad(),
                failure.getEstado(),
                failure.getFechaReporte() == null ? "" : DISPLAY_FORMAT.format(failure.getFechaReporte()));
    }

    private Long extractId(String code) {
        try {
            return Long.parseLong(code.replace("FAL-", "").trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Codigo de falla invalido");
        }
    }
}