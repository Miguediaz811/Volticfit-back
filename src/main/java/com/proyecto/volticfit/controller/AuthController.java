package com.proyecto.volticfit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.volticfit.dto.ChangePasswordRequestDTO;
import com.proyecto.volticfit.dto.ForgotPasswordRequestDTO;
import com.proyecto.volticfit.dto.LoginRequestDTO;
import com.proyecto.volticfit.dto.LoginResponseDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.RefreshTokenResponseDTO;
import com.proyecto.volticfit.dto.RegisterRequestDTO;
import com.proyecto.volticfit.dto.RestorePasswordRequestDTO;
import com.proyecto.volticfit.dto.VerifyCodeRequestDTO;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.AuthService;
import com.proyecto.volticfit.service.TokenBlackListService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;
    private final TokenBlackListService blacklistService;

    // Público
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

    // Público
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

    // Autenticado
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            blacklistService.add(token);
            return ResponseEntity.ok(Map.of("message", "Session closed"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Token not provided"));
    }

    // Público
    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Authorization header missing"));
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

    // Público
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.verifyRecoveryCode(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Recovery process initiated"));
        }
    }

    // Público
    @PostMapping("/recovery/verify")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequestDTO request) {
        try {
            MessageResponseDTO response = authService.verifyRecoveryCode(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/recovery/reset")
    public ResponseEntity<?> resetPassword(@RequestBody RestorePasswordRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.restorePassword(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Solo ADMIN
    @GetMapping("/listar")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<?> getUsers() {
        try {
            List<Users> users = authService.getAllUsers();
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}