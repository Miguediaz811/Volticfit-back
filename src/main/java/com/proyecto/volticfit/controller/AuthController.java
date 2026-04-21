package com.proyecto.volticfit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.volticfit.dto.ChangePasswordRequestDTO;
import com.proyecto.volticfit.dto.LoginRequestDTO;
import com.proyecto.volticfit.dto.LoginResponseDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.RefreshTokenResponseDTO;
import com.proyecto.volticfit.dto.RegisterRequestDTO;
import com.proyecto.volticfit.service.AuthService;
import com.proyecto.volticfit.service.TokenBlackListService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // <--- AÑADE ESTA LÍNEA justo debajo de @RequestMapping
public class AuthController {

    private final AuthService authService;
    private final TokenBlackListService blacklistService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            MessageResponseDTO response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            blacklistService.add(token);
            return ResponseEntity.ok(Map.of("message", "Sesión cerrada"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Token no proporcionado"));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Header Authorization faltante"));
        }

        String token = authHeader.substring(7);
        try {
            RefreshTokenResponseDTO response = authService.refreshToken(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponseDTO> changePassword(
        @Valid @RequestBody ChangePasswordRequestDTO request,
        HttpServletRequest httpRequest) {

    Long userId = (Long) httpRequest.getAttribute("userId");

    return ResponseEntity.ok(authService.changePassword(userId, request));
}
}
