package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.Machine.FailureRequestDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.service.EquipmentFailureService;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.enums.RoleEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fallas")
@RequiredArgsConstructor
public class EquipmentFailureController {

    private final EquipmentFailureService failureService;

    // Tarea 1.3: Registrar una falla
    @PostMapping
    public ResponseEntity<MessageResponseDTO> registerFailure(@RequestBody FailureRequestDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        failureService.registerFailure(userId, dto);
        
        MessageResponseDTO res = new MessageResponseDTO();
        res.setMessage("Falla registrada correctamente");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // Tarea 1.8: Consultar fallas (Solo Admin)
    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<?> getAllFailures() {
        return ResponseEntity.ok(failureService.getAllFailures());
    }
}