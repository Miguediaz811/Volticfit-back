package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.entity.Sanction;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.SanctionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sanctions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SanctionController {

    /**
     * servicio de sanciones
     */
    private final SanctionService sanctionService;


    @Operation(summary = "List sanctions - ADMIN sees all, user sees only their own",
            responses = {
                @ApiResponse(responseCode = "200", description = "Sanctions retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Sanction.class))),
                @ApiResponse(responseCode = "400", description = "Error retrieving sanctions",
                        content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
            })
    @GetMapping
    public ResponseEntity<Object> getSanctions(HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            Long userId = (Long) request.getAttribute("userId");
 
            List<Sanction> sanctions;
 
            if (RoleEnum.ADMIN.getValue().equalsIgnoreCase(role)) {
                sanctions = sanctionService.getAll();
            } else {
                sanctions = sanctionService.getByUser(userId);
            }
 
            if (sanctions.isEmpty()) {
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("No sanctions found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
 
            return ResponseEntity.ok(sanctions);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
 
    @Operation(summary = "Get sanction by ID - ADMIN only",
            responses = {
                @ApiResponse(responseCode = "200", description = "Sanction retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Sanction.class))),
                @ApiResponse(responseCode = "400", description = "Sanction not found",
                        content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
                @ApiResponse(responseCode = "403", description = "Access denied",
                        content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
            })
    @GetMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<Object> getSanctionById(@PathVariable Long id) {
        try {
            Sanction sanction = sanctionService.getById(id);
            return ResponseEntity.ok(sanction);
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
