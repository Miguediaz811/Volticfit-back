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
import com.proyecto.volticfit.service.TokenBlackListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
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
     * * @param request datos del login
     * @return LoginResponseDTO con el token y datos del usuario
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
     * * @param request datos de la solicitud
     * @return MessageResponseDTO con el mensaje de éxito o error
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
     * * @param request datos de la solicitud 
     * @return RefreshTokenResponseDTO con el nuevo token o mensaje de error
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

    // --- BLOQUE DE RECUPERACIÓN EDITADO ---

    /**
     * Inicia el proceso de recuperación enviando un código/enlace al email proporcionado.
     * * @param request DTO con el correo del usuario.
     * @return ResponseEntity con MessageResponseDTO informando el estado del proceso.
     */
    @Operation(summary = "Forgot password",
        responses = {
            @ApiResponse(responseCode = "200", description = "Recovery process initiated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
        }
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        try {
            log.info("📧 Procesando solicitud de recuperación para: {}", request.getEmail());
            return ResponseEntity.ok(authService.processForgotPassword(request.getEmail()));
        } catch (Exception e) {
            MessageResponseDTO response = new MessageResponseDTO();
            response.setMessage("Si el correo existe, recibirá instrucciones en breve.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    /**
     * Valida si el código de recuperación ingresado por el usuario es correcto y vigente.
     * * @param request DTO con el código de verificación y email.
     * @return ResponseEntity con MessageResponseDTO confirmando la validez del código.
     */
    @Operation(summary = "Verify recovery code",
        responses = {
            @ApiResponse(responseCode = "200", description = "Code verified successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired code")
        }
    )
    @PostMapping("/recovery/verify")
    public ResponseEntity<MessageResponseDTO> verifyCode(@Valid @RequestBody VerifyCodeRequestDTO request) {
        try {
            log.info("🛡️ Verificando código de recuperación.");
            MessageResponseDTO response = authService.verifyRecoveryCode(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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
            @ApiResponse(responseCode = "400", description = "User not found")
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