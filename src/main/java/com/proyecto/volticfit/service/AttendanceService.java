package com.proyecto.volticfit.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Attendance.AttendanceResponseDTO;
import com.proyecto.volticfit.dto.Attendance.CreateAttendanceDTO;
import com.proyecto.volticfit.entity.Attendance;
import com.proyecto.volticfit.entity.Audit;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.AttendanceRepository;
import com.proyecto.volticfit.repository.AuditRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio para gestionar el registro manual de asistencia (HU13).
 * Maneja validaciones de coherencia, auditoría y control de seguridad.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AttendanceService {

    /** Límite de registros manuales antes de requerir contraseña adicional */
    private static final int MANUAL_LIMIT = 15;

    private final AttendanceRepository attendanceRepository;
    private final AuditRepository auditRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Contraseña de seguridad adicional inyectada desde application.yaml.
     * Se solicita cuando el sistema supera los 15 registros manuales.
     */
    @Value("${security.manual-attendance-password:admin1234}")
    private String manualSecurityPassword;

    /**
     * Registra manualmente una asistencia (ENTRADA o SALIDA).
     * Aplica validaciones de coherencia, control de seguridad y auditoría.
     *
     * @param request  DTO con los datos del registro
     * @param adminId  ID del administrador que realiza el registro
     * @return MessageResponseDTO con el resultado
     */
    @Transactional
    public MessageResponseDTO registerManualAttendance(CreateAttendanceDTO request, Long adminId) {

        // 1. Validar que el usuario existe
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Validar que el admin existe
        Users admin = usersRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        // 3. Control de seguridad: si hay más de 15 registros manuales, validar contraseña adicional
        long totalManualRecords = attendanceRepository.countByIsManualTrue();
        if (totalManualRecords >= MANUAL_LIMIT) {
            if (request.getSecurityPassword() == null || request.getSecurityPassword().isBlank()) {
                throw new RuntimeException("Se requiere contraseña de seguridad adicional para continuar (límite de registros manuales alcanzado)");
            }
            if (!manualSecurityPassword.equals(request.getSecurityPassword())) {
                throw new RuntimeException("Contraseña de seguridad incorrecta");
            }
        }

        // 4. Validar coherencia con el historial del día
        validateCoherence(request, user);

        // 5. Crear y guardar el registro de asistencia
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setType(request.getType());
        attendance.setIsManual(true);
        attendance.setDate(request.getDate());
        attendance.setTime(request.getTime());
        attendance.setReason(request.getReason());
        attendance.setRegisteredBy(admin);
        attendanceRepository.save(attendance);

        // 6. Registrar en auditoría
        saveAuditLog(admin, user,
                "REGISTRO_MANUAL_" + request.getType(),
                String.format("Registro manual de %s para el usuario %s %s. Motivo: %s",
                        request.getType(), user.getNames(), user.getSurnames(), request.getReason()));

        log.info("Registro manual de {} registrado para usuario ID: {} por admin ID: {}",
                request.getType(), user.getIdUser(), adminId);

        return new MessageResponseDTO("Registro de " + request.getType() + " registrado exitosamente");
    }

    /**
     * Obtiene el historial de asistencia de un usuario específico.
     *
     * @param userId ID del usuario
     * @return lista de registros de asistencia
     */
    public List<AttendanceResponseDTO> getAttendanceByUser(Long userId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return attendanceRepository.findByUserIdUserOrderByDateDescTimeDesc(userId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Obtiene todos los registros de asistencia del sistema (solo admin).
     *
     * @return lista completa de registros
     */
    public List<AttendanceResponseDTO> getAllAttendance() {
        return attendanceRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Valida la coherencia del registro con el historial del usuario en el día.
     * - No se puede registrar ENTRADA si ya hay una ENTRADA sin SALIDA.
     * - No se puede registrar SALIDA si no hay una ENTRADA previa.
     */
    private void validateCoherence(CreateAttendanceDTO request, Users user) {
        List<Attendance> todayRecords = attendanceRepository
                .findByUserAndDate(user.getIdUser(), request.getDate());

        if (todayRecords.isEmpty()) {
            // Si no hay registros hoy, solo se puede registrar ENTRADA
            if ("SALIDA".equals(request.getType())) {
                throw new RuntimeException("No se puede registrar SALIDA sin una ENTRADA previa en el día");
            }
            return;
        }

        // El último registro del día
        Attendance lastRecord = todayRecords.get(0);

        if ("ENTRADA".equals(request.getType()) && "ENTRADA".equals(lastRecord.getType())) {
            throw new RuntimeException("El usuario ya tiene una ENTRADA registrada hoy sin SALIDA. Registre la SALIDA primero.");
        }

        if ("SALIDA".equals(request.getType()) && "SALIDA".equals(lastRecord.getType())) {
            throw new RuntimeException("El usuario ya tiene una SALIDA registrada. Registre primero una nueva ENTRADA.");
        }
    }

    /**
     * Guarda un log en la tabla de auditoría.
     */
    private void saveAuditLog(Users admin, Users affectedUser, String action, String detail) {
        Audit audit = new Audit();
        audit.setAdmin(admin);
        audit.setAffectedUser(affectedUser);
        audit.setAction(action);
        audit.setDetail(detail);
        audit.setDateTime(LocalDateTime.now());
        auditRepository.save(audit);
    }

    /**
     * Convierte una entidad Attendance a su DTO de respuesta.
     */
    private AttendanceResponseDTO mapToResponseDTO(Attendance a) {
        AttendanceResponseDTO dto = new AttendanceResponseDTO();
        dto.setIdAttendance(a.getIdAttendance());
        dto.setUserId(a.getUser().getIdUser());
        dto.setUserName(a.getUser().getNames() + " " + a.getUser().getSurnames());
        dto.setType(a.getType());
        dto.setIsManual(a.getIsManual());
        dto.setDate(a.getDate());
        dto.setTime(a.getTime());
        dto.setReason(a.getReason());
        dto.setRegisteredByName(a.getRegisteredBy().getNames() + " " + a.getRegisteredBy().getSurnames());
        return dto;
    }
}