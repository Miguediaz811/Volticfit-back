package com.proyecto.volticfit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

<<<<<<< HEAD
import com.proyecto.volticfit.dto.LoginRequestDTO;
import com.proyecto.volticfit.dto.LoginResponseDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.RefreshTokenResponseDTO;
import com.proyecto.volticfit.dto.RegisterRequestDTO;
=======
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Auth.ForgotPasswordRequestDTO;
import com.proyecto.volticfit.dto.Auth.LoginRequestDTO;
import com.proyecto.volticfit.dto.Auth.LoginResponseDTO;
import com.proyecto.volticfit.dto.Auth.RefreshTokenResponseDTO;
import com.proyecto.volticfit.dto.Auth.RegisterRequestDTO;
import com.proyecto.volticfit.dto.Auth.RestorePasswordRequestDTO;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
>>>>>>> b19ca583b364e99cd7cd83e250db446e53962c6f
import com.proyecto.volticfit.service.AuthService;
import com.proyecto.volticfit.service.JwtService;
import com.proyecto.volticfit.service.TokenBlackListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;

import java.time.Instant;

import java.util.List;

/**
 * Controlador para gestionar la autenticación y recuperación de cuentas
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
<<<<<<< HEAD
=======
@Log4j2
@CrossOrigin(origins = "http://localhost:4200")
>>>>>>> b19ca583b364e99cd7cd83e250db446e53962c6f
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

    /**
     * servicio de JWT
     */
    private final JwtService jwtService;

    @Operation(summary = "Register a new user",
        responses = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Email already in use")
        }
    )
    /**
     * Registro de usuarios
     * * @param request datos del registro
     * @return MessageResponseDTO 
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register( @Valid @RequestBody RegisterRequestDTO request) {
        try {
            MessageResponseDTO response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new java.util.HashMap<>(java.util.Map.of("error", e.getMessage())));
=======
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
>>>>>>> b19ca583b364e99cd7cd83e250db446e53962c6f
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
     * * @param request datos del login
     * @return LoginResponseDTO con el token y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new java.util.HashMap<>(java.util.Map.of("error", e.getMessage())));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            blacklistService.add(token);
            return ResponseEntity.ok(new java.util.HashMap<>(java.util.Map.of("message", "Sesión cerrada")));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new java.util.HashMap<>(java.util.Map.of("error", "Token no proporcionado")));
    }

=======
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
     * * @param request datos de la solicitud
     * @return MessageResponseDTO con el mensaje de éxito o error
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDTO> logout(HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        MessageResponseDTO response = new MessageResponseDTO();

        if (token != null && token.startsWith("Bearer ")) {
            
            token = token.substring(7);
            
            Instant expiration = jwtService.extractClaims(token, claims -> claims.getExpiration().toInstant());
            blacklistService.blacklistToken(token, expiration);
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
     * * @param request datos de la solicitud 
     * @return RefreshTokenResponseDTO con el nuevo token o mensaje de error
     */
>>>>>>> b19ca583b364e99cd7cd83e250db446e53962c6f
    @GetMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new java.util.HashMap<>(java.util.Map.of("error", "Header Authorization faltante")));
=======
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
>>>>>>> b19ca583b364e99cd7cd83e250db446e53962c6f
        }

        String token = authHeader.substring(7);

        try {
            RefreshTokenResponseDTO response = authService.refreshToken(token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new java.util.HashMap<>(java.util.Map.of("error", e.getMessage())));
        }
    }
}
=======
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // --- BLOQUE DE RECUPERACIÓN EDITADO ---

    /**
     * Inicia el proceso de recuperación enviando un código/enlace al email proporcionado.
     * * @param request DTO con el correo del usuario.
     * @return ResponseEntity con MessageResponseDTO informando el estado del proceso.
     */
    @Operation(summary = "Forgot password",
        responses = {
            @ApiResponse(responseCode = "200", description = "Recovery process initiated"),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        try {
            log.info("📧 Procesando solicitud de recuperación para: {}", request.getEmail());
            return ResponseEntity.ok(authService.verifyRecoveryCode(request));
        } catch (Exception e) {
            MessageResponseDTO response = new MessageResponseDTO();
            response.setMessage("Si el correo existe, recibirá instrucciones en breve.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    /**
     * Finaliza el proceso de recuperación estableciendo una nueva contraseña.
     * * @param request DTO con el token de validación y la nueva clave.
     * @return ResponseEntity con MessageResponseDTO confirmando el éxito del cambio.
     */
    @Operation(summary = "Reset password",
        responses = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error during reset")
        }
    )
    @PostMapping("/recovery/reset")
    public ResponseEntity<MessageResponseDTO> resetPassword(@Valid @RequestBody RestorePasswordRequestDTO request) {
        try {
            log.info("🔑 Actualizando contraseña del usuario.");
            return ResponseEntity.ok(authService.restorePassword(request));
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // --- FIN DEL BLOQUE DE RECUPERACIÓN ---

    @Operation(summary = "List all active users - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )
    /**
     * Listar usuarios
     * * @return List<Users> con la lista de usuarios
     */
    @GetMapping("/listar")
    @RequiresRole(RoleEnum.ADMIN)
    public ResponseEntity<List<Users>> getUsers() {
        try {
            List<Users> users = authService.getAllUsers();
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
>>>>>>> b19ca583b364e99cd7cd83e250db446e53962c6f
