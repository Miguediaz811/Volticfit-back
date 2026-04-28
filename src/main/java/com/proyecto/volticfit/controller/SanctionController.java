package com.proyecto.volticfit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @ApiResponse(responseCode = "400", description = "Error retrieving sanctions")
        }
    )

    /**
     * Listar sanciones
     * 
     * @param request datos de la solicitud para obtener el rol y userId del usuario
     * @return List<Sanction> con la lista de sanciones del usuario o todas las sanciones si es ADMIN
     */
    @GetMapping
    public ResponseEntity<List<Sanction>> getSanctions(HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            Long userId = (Long) request.getAttribute("userId");
 
            if (RoleEnum.ADMIN.getValue().equalsIgnoreCase(role)) {
                return ResponseEntity.ok(sanctionService.getAll());
            } else {
                return ResponseEntity.ok(sanctionService.getByUser(userId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

 
    @Operation(summary = "Get sanction by ID - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "200", description = "Sanction retrieved successfully",
                content = @Content(schema = @Schema(implementation = Sanction.class))),
            @ApiResponse(responseCode = "400", description = "Sanction not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )

    /**
     * Obtener sanción por ID
     * 
     * @param id ID de la sanción
     * @return Sanction con los datos de la sanción o mensaje de error
     */
    @GetMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<?> getSanctionById(@PathVariable Long id) {
        try {
            Sanction sanction = sanctionService.getById(id);
            return ResponseEntity.ok(sanction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
