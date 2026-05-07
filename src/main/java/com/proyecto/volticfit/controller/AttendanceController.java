package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Attendance.AttendanceResponseDTO;
import com.proyecto.volticfit.dto.Attendance.CreateAttendanceDTO;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.AttendanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para gestionar el registro manual de asistencia (HU13).
 * Solo accesible por administradores.
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "Registrar asistencia manual - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "201", description = "Asistencia registrada exitosamente",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación o coherencia"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @PostMapping("/manual")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> registerManualAttendance(
            @Valid @RequestBody CreateAttendanceDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long adminId = (Long) httpRequest.getAttribute("userId");
            MessageResponseDTO response = attendanceService.registerManualAttendance(request, adminId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponseDTO("Error en el registro, intente nuevamente."));
        }
    }

    @Operation(summary = "Obtener historial de asistencia de un usuario - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @GetMapping("/user/{userId}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Object> getAttendanceByUser(@PathVariable Long userId) {
        try {
            List<AttendanceResponseDTO> records = attendanceService.getAttendanceByUser(userId);
            if (records.isEmpty()) {
                return ResponseEntity.ok(new MessageResponseDTO("No hay registros de asistencia para este usuario"));
            }
            return ResponseEntity.ok(records);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponseDTO(e.getMessage()));
        }
    }

    @Operation(summary = "Obtener todos los registros de asistencia - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Object> getAllAttendance() {
        try {
            List<AttendanceResponseDTO> records = attendanceService.getAllAttendance();
            if (records.isEmpty()) {
                return ResponseEntity.ok(new MessageResponseDTO("No hay registros de asistencia"));
            }
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponseDTO(e.getMessage()));
        }
    }
}