package com.proyecto.volticfit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Auth.ForgotPasswordRequestDTO;
import com.proyecto.volticfit.dto.Auth.LoginRequestDTO;
import com.proyecto.volticfit.dto.Auth.LoginResponseDTO;
import com.proyecto.volticfit.dto.Auth.RefreshTokenResponseDTO;
import com.proyecto.volticfit.dto.Auth.RegisterRequestDTO;
import com.proyecto.volticfit.dto.Auth.RestorePasswordRequestDTO;
import com.proyecto.volticfit.dto.Auth.VerifyCodeRequestDTO;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.AuthService;
import com.proyecto.volticfit.service.PasswordResetService;
import com.proyecto.volticfit.service.TokenBlackListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestionar la autenticación y recuperación de cuentas
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    /**
     * servicio de auth
     */
    private final AuthService authService;
    
    /**
     * servicio de lista negra del token
     */
    private final TokenBlackListService blacklistService;

    /**
     * servicio de recuperación de contraseña
     */
    private final PasswordResetService passwordResetService;

    @Operation(summary = "Register a new user",
        responses = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Email already in use")
        }
    )
    /**
     * Registro de usuarios

     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register( @Valid @RequestBody RegisterRequestDTO request) {
        try {
            MessageResponseDTO response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Login",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials or inactive account")
        }
    )
    /**
     * Login de usuarios

     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            LoginResponseDTO error = new LoginResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @Operation(summary = "Logout - invalidates the current token",
        responses = {
            @ApiResponse(responseCode = "200", description = "Session closed"),
            @ApiResponse(responseCode = "400", description = "Token not provided")
        }
    )
    /**
     * Cierre de sesión

     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDTO> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        MessageResponseDTO response = new MessageResponseDTO();
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            blacklistService.add(token);
            response.setMessage("Session closed");
            return ResponseEntity.ok(response);
        }
        response.setMessage("Token not provided");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Operation(summary = "Refresh token",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing token"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
        }
    )
    /**
     * Refrescar token

     */
    @GetMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String token = authHeader.substring(7);

        try {
            RefreshTokenResponseDTO response = authService.refreshToken(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @Operation(summary = "Forgot password",
        responses = {
            @ApiResponse(responseCode = "200", description = "Recovery process initiated"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    /**
     * Recuperación de contraseña - Genera y envía el código al correo

     */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        try {
            return ResponseEntity.ok(passwordResetService.forgotPassword(request));
        } catch (Exception e) {
            MessageResponseDTO response = new MessageResponseDTO();
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "Verify recovery code",
        responses = {
            @ApiResponse(responseCode = "200", description = "Code verified successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired code")
        }
    )
    /**
     * Verificación del código de recuperación

     */
    @PostMapping("/recovery/verify")
    public ResponseEntity<MessageResponseDTO> verifyCode(@Valid @RequestBody VerifyCodeRequestDTO request) {
        try {
            MessageResponseDTO response = passwordResetService.verifyCode(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "Reset password",
        responses = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error during reset")
        }
    )
    /**
     * Restablecimiento final de contraseña

     */
    @PostMapping("/recovery/reset")
    public ResponseEntity<MessageResponseDTO> resetPassword(@Valid @RequestBody RestorePasswordRequestDTO request) {
        try {
            return ResponseEntity.ok(passwordResetService.restorePassword(request));
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "List all active users - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    /**
     * Listar usuarios
     */
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