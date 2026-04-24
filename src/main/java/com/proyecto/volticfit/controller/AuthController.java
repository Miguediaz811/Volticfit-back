package com.proyecto.volticfit.controller;

import com.proyecto.volticfit.dto.*;
import com.proyecto.volticfit.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenBlackListService tokenBlackListService;

    @Autowired
    private PasswordResetCodeService passwordResetCodeService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        if (request.getCorreo() == null || request.getCorreo().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo es obligatorio"));
        }

        // Generamos el token de 15 minutos con tu JwtService
        String token = jwtService.generateResetToken(request.getCorreo());
        
        // Enviamos el correo con tu EmailService
        emailService.enviarCorreoRecuperacion(request.getCorreo(), token);
        
        return ResponseEntity.ok(Map.of("message", "Enlace enviado con éxito"));
    }
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequestDTO request) {
        boolean isValid = passwordResetCodeService.isValidCode(request.getCorreo(), request.getCodigo());
        
        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "Código válido"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Código inválido o expirado"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody Map<String, String> body) {
        String nuevaPassword = body.get("nuevaContrasena");

        // 1. Verificamos si el token está en la lista negra (ya usado)
        if (tokenBlackListService.isBlacklisted(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Este enlace ya fue utilizado"));
        }

        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "el tiempo ya expiro"));
        }

        // 3. Extraemos el correo para identificar al usuario
        String correo = jwtService.extractCorreo(token);
        System.out.println("Cambiando contraseña para: " + correo);

        // 4. Invalidamos el token para que no se use de nuevo
        tokenBlackListService.add(token);
        
        return ResponseEntity.ok(Map.of("message", "¡Contraseña actualizada con éxito!"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO request, @RequestHeader("Authorization") String token) {
        // Validar token actual
        String jwt = token.substring(7);
        if (!jwtService.isTokenValid(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Lógica de validación de password actual vs nueva...
        return ResponseEntity.ok(Map.of("message", "Contraseña cambiada correctamente"));
    }
}