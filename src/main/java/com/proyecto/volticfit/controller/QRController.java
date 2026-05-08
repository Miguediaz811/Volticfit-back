package com.proyecto.volticfit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.QrResponseDTO;
import com.proyecto.volticfit.dto.QrValidationResponseDTO;
import com.proyecto.volticfit.service.JwtService;
import com.proyecto.volticfit.service.QRService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
public class QRController {

    private final QRService qrService;

    private final JwtService jwtService;

        /**
     * Endpoint para generar un código QR dinámico.
     *
     * <p>Requiere que el usuario esté autenticado.
     * Obtiene el email y rol desde el token JWT actual.</p>
     *
     * @param request petición HTTP con atributos de autenticación
     * @return DTO con la información del QR
     */
    @GetMapping("/generate")
    public ResponseEntity<QrResponseDTO> generate(HttpServletRequest request) {

        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");

        String qr = qrService.generateQr(email, role);

        return ResponseEntity.ok(new QrResponseDTO(qr));
    }

        /**
     * Endpoint para validar un código QR escaneado.
     *
     * @param token token contenido en el QR
     * @return resultado de validación
     */
    @GetMapping("/validate")
    public ResponseEntity<QrValidationResponseDTO> validate(@RequestParam String token) {

        return ResponseEntity.ok(qrService.validateQr(token));
    }

}