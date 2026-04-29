package com.proyecto.volticfit.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Auth.ChangePasswordRequestDTO;
import com.proyecto.volticfit.dto.Auth.ForgotPasswordRequestDTO;
import com.proyecto.volticfit.dto.Auth.LoginRequestDTO;
import com.proyecto.volticfit.dto.Auth.LoginResponseDTO;
import com.proyecto.volticfit.dto.Auth.RefreshTokenResponseDTO;
import com.proyecto.volticfit.dto.Auth.RegisterRequestDTO;
import com.proyecto.volticfit.dto.Auth.RestorePasswordRequestDTO;
import com.proyecto.volticfit.dto.Auth.VerifyCodeRequestDTO;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio central para la gestión de seguridad, autenticación y ciclo de vida de usuarios.
 * Maneja el registro, validación de credenciales y flujos de recuperación de cuenta.
 */
@Service
@Log4j2 // 1. Agregado para solucionar el error de "symbol: variable log"
@RequiredArgsConstructor
public class AuthService {

    private final EmailService emailService;

    // Inyectamos el PasswordEncoder para manejar el hashing de contraseñas
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;

    // Inyectamos el JwtService para manejar la generación y validación de tokens
    // JWT
    private final JwtService jwtService;
    private final EmailService emailService;

    public MessageResponseDTO register(RegisterRequestDTO request) {
        if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Este correo ya está en uso");
        }
        Users user = new Users();
        user.setNames(request.getNames());
        user.setSurnames(request.getSurnames());
        user.setDocType(request.getDocType());
        user.setDocNum(request.getDocNum());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setState(request.getState());
        usersRepository.save(user);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Registro exitoso");
        return response;
    }

    /**
     * Autentica al usuario y genera un token JWT si las credenciales son válidas y la cuenta está activa.
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.getState()) {
            throw new RuntimeException("This account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String jwt = jwtService.generateToken(user.getEmail(), user.getRole());
        LoginResponseDTO response = new LoginResponseDTO();
        response.setMessage("Inicio de sesión exitoso");
        response.setJwt(jwt);
        return response;
    }

    public RefreshTokenResponseDTO refreshToken(String token) {
        String jwt = jwtService.refreshToken(token);
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO();
        response.setJwt(jwt);
        return response;
    }

    /**
     * Cambia la contraseña de un usuario dado su ID, verificando primero la contraseña
     *
     * @param userId  el ID del usuario que desea cambiar su contraseña
     * @param request el objeto que contiene la contraseña actual y la nueva contraseña
     * @return mensaje de respuesta indicando el resultado de la operación
     */
    @Transactional
    public MessageResponseDTO changePassword(Long userId, ChangePasswordRequestDTO request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
 
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
 
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Contraseña actualizada correctamente");
        return response;
    }
    /**
     * Este método se encarga de actualizar la contraseña sin necesidad de conocer
     * la actual,
     * ideal para casos de recuperación de contraseña. Se asume que el proceso de
     * verificación
     * (como un token enviado por email) se ha manejado antes de llamar a este
     * método.
     */
    @Transactional
    public MessageResponseDTO restorePassword(RestorePasswordRequestDTO request) {
        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Contraseña actualizada con éxito");
        return response;
    }

 /**
     * Sends a password recovery email with a reset token.
     *
     * @param request the forgot password request with the user's email
     * @return success message
     */
    public MessageResponseDTO verifyRecoveryCode(ForgotPasswordRequestDTO request) {
        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
 
        String token = jwtService.generateResetToken(user.getEmail());
        emailService.enviarCorreoRecuperacion(user.getEmail(), token);
 
        log.info("Recovery email sent to: {}", user.getEmail());
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("If the email is registered, you will receive a recovery link.");
        return response;
    }
 
    /**
     * Verifies the recovery token sent to the user's email.
     *
     * @param request the email and recovery token
     * @return success message
     * @throws RuntimeException if the token is invalid or expired
     */
    public MessageResponseDTO verifyRecoveryCode(VerifyCodeRequestDTO request) {
    // Cambiamos getToken() por getCode() para que coincida con el DTO
    if (!jwtService.isTokenValid(request.getCode())) { 
        throw new RuntimeException("Invalid or expired recovery token");
    }

    // Listar usuarios activos
    public List<Users> getAllUsers() {
        return usersRepository.findByStateTrue();
    }
}