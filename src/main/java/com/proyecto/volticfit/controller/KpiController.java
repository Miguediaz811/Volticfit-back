package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.KpiDTO;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.enums.RoleEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/kpis")
public class KpiController {

    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<List<KpiDTO>> obtenerKpis() {
        // Simulación: Aquí conectarías con tu KpiService futuro
        List<KpiDTO> kpis = List.of(
            new KpiDTO("Usuarios Activos", 125.0, "Total de usuarios activos en el sistema"),
            new KpiDTO("Reservas de hoy", 40.0, "Total de reservas agendadas para hoy")
        );
        return ResponseEntity.ok(kpis);
    }
}