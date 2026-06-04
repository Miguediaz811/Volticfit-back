package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.service.AuditService;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<?> obtenerAuditoria(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long usuarioIdFiltro) {
            
        return ResponseEntity.ok(auditService.obtenerHistorial(usuarioIdFiltro, PageRequest.of(page, size)));
    }
}