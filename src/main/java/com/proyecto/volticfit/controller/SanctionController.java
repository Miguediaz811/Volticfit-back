package com.proyecto.volticfit.controller;

import java.util.List;
import java.util.Map;

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

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sanctions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SanctionController {

    private final SanctionService sanctionService;

    // ADMIN ve todas, aprendiz ve solo las suyas
    @GetMapping
    public ResponseEntity<?> getSanctions(HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            Long userId = (Long) request.getAttribute("userId");
 
            if (RoleEnum.ADMIN.getValue().equalsIgnoreCase(role)) {
                List<Sanction> sanctions = sanctionService.getAll();
                return ResponseEntity.ok(sanctions);
            } else {
                List<Sanction> sanctions = sanctionService.getByUser(userId);
                return ResponseEntity.ok(sanctions);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Solo ADMIN
    @GetMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<?> getSanctionById(@PathVariable Long id) {
        try {
            Sanction sanction = sanctionService.getById(id);
            return ResponseEntity.ok(sanction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
