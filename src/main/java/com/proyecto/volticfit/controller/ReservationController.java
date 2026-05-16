package com.proyecto.volticfit.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Reservations.CreateReservationDTO;
import com.proyecto.volticfit.dto.Reservations.ShiftResponseDTO;
import com.proyecto.volticfit.entity.Reservation;
import com.proyecto.volticfit.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Gym shift reservation endpoints")
public class ReservationController {
 
    private final ReservationService reservationService;
 
    @Operation(summary = "Get available shifts for a date",
        responses = {
            @ApiResponse(responseCode = "200", description = "Shifts retrieved successfully",
                content = @Content(schema = @Schema(implementation = ShiftResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date")
        }
    )
    @GetMapping("/shifts")
    public ResponseEntity<Object> getAvailableShifts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<ShiftResponseDTO> shifts = reservationService.getAvailableShifts(date);
            return ResponseEntity.ok(shifts);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
 
    @Operation(summary = "Create a reservation for a shift",
        responses = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "No spots available or already reserved")
        }
    )
    @PostMapping
    public ResponseEntity<MessageResponseDTO> createReservation(
            @Valid @RequestBody CreateReservationDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            MessageResponseDTO response = reservationService.createReservation(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
 
    @Operation(summary = "Cancel a reservation",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> cancelReservation(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            String role = (String) httpRequest.getAttribute("role");
            MessageResponseDTO response = reservationService.cancelReservation(id, userId, role);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
 
    @Operation(summary = "Get all reservations for the authenticated user",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
        }
    )
    @GetMapping("/my-reservations")
    public ResponseEntity<Object> getUserReservations(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            List<Reservation> reservations = reservationService.getUserReservations(userId);
            if (reservations.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No reservations found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}