package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.Rutinas.RestaurarVersionRequestDTO;
import com.proyecto.volticfit.dto.Rutinas.RutinaRequestDTO;
import com.proyecto.volticfit.dto.Rutinas.RutinaResponseDTO;
import com.proyecto.volticfit.dto.Rutinas.RutinaVersionResponseDTO;
import com.proyecto.volticfit.service.RutinaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rutinas")
@RequiredArgsConstructor
public class RutinaController {

    private final RutinaService rutinaService;

    // GET /rutinas — listar todas las rutinas del usuario autenticado
    @GetMapping
    public ResponseEntity<?> listar(HttpServletRequest request) {
        String correo = (String) request.getAttribute("correo");
        try {
            List<RutinaResponseDTO> rutinas = rutinaService.listarRutinas(correo);
            return ResponseEntity.ok(rutinas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No fue posible completar la acción, intente nuevamente"));
        }
    }

    // POST /rutinas — crear rutina
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody RutinaRequestDTO body,
                                   HttpServletRequest request) {
        String correo = (String) request.getAttribute("correo");
        try {
            RutinaResponseDTO response = rutinaService.crearRutina(body, correo);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No fue posible completar la acción, intente nuevamente"));
        }
    }

    // PUT /rutinas/{id} — actualizar rutina (genera nueva versión automáticamente)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody RutinaRequestDTO body,
                                        HttpServletRequest request) {
        String correo = (String) request.getAttribute("correo");
        try {
            RutinaResponseDTO response = rutinaService.actualizarRutina(id, body, correo);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No fue posible completar la acción, intente nuevamente"));
        }
    }

    // GET /rutinas/{id}/historial — consultar historial de versiones
    @GetMapping("/{id}/historial")
    public ResponseEntity<?> historial(@PathVariable Long id,
                                       HttpServletRequest request) {
        String correo = (String) request.getAttribute("correo");
        try {
            List<RutinaVersionResponseDTO> historial = rutinaService.consultarHistorial(id, correo);
            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No fue posible completar la acción, intente nuevamente"));
        }
    }

    // POST /rutinas/{id}/restaurar — restaurar una versión anterior
    @PostMapping("/{id}/restaurar")
    public ResponseEntity<?> restaurar(@PathVariable Long id,
                                       @Valid @RequestBody RestaurarVersionRequestDTO body,
                                       HttpServletRequest request) {
        String correo = (String) request.getAttribute("correo");
        try {
            RutinaResponseDTO response = rutinaService.restaurarVersion(id, body, correo);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No fue posible completar la acción, intente nuevamente"));
        }
    }
}