package com.proyecto.volticfit.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Reservation.AforoResponseDTO;
import com.proyecto.volticfit.dto.Reservation.CreateReservationDTO;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para gestionar el control de aforo por franja horaria (HU27).
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "Consultar aforo por franja horaria - ADMIN only",
        description = "Retorna el aforo actual de cada franja horaria para una fecha. Si no se indica fecha, usa hoy.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Aforo obtenido exitosamente",
                content = @Content(schema = @Schema(implementation = AforoResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @GetMapping("/aforo")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<AforoResponseDTO> getAforo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        AforoResponseDTO response = reservationService.getAforoByDate(targetDate);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener franjas horarias disponibles",
        responses = {
            @ApiResponse(responseCode = "200", description = "Franjas obtenidas exitosamente")
        }
    )
    @GetMapping("/slots")
    public ResponseEntity<List<String>> getAvailableSlots() {
        return ResponseEntity.ok(reservationService.getAvailableTimeSlots());
    }

    @Operation(summary = "Crear reserva en una franja horaria - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Aforo completo o datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @PostMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> createReservation(@Valid @RequestBody CreateReservationDTO request) {
        try {
            MessageResponseDTO response = reservationService.createReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponseDTO("Error al crear la reserva, intente nuevamente."));
        }
    }

    @Operation(summary = "Cancelar reserva - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @DeleteMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<MessageResponseDTO> cancelReservation(@PathVariable Long id) {
        try {
            MessageResponseDTO response = reservationService.cancelReservation(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponseDTO(e.getMessage()));
        }
    }
}