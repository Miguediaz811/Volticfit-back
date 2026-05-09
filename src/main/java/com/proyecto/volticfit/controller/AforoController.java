package com.proyecto.volticfit.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Reservation.AforoResponseDTO;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.AforoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para gestionar el control de aforo del gimnasio (HU27).
 * Muestra cuántas personas están físicamente dentro del gimnasio por franja horaria.
 */
@RestController
@RequestMapping("/api/aforo")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AforoController {

    private final AforoService aforoService;

    @Operation(summary = "Consultar aforo actual por franja horaria - ADMIN only",
        description = "Retorna el número de personas dentro del gimnasio por franja horaria. " +
                      "Cuenta asistencias con ENTRADA sin SALIDA. Máximo 20 personas.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Aforo obtenido exitosamente",
                content = @Content(schema = @Schema(implementation = AforoResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Object> getAforo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            LocalDate targetDate = (date != null) ? date : LocalDate.now();
            AforoResponseDTO response = aforoService.getAforoByDate(targetDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponseDTO(e.getMessage()));
        }
    }
}