package com.proyecto.volticfit.service;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.*;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordResetCodeService passwordResetCodeService; 

    public MessageResponseDTO register(RegisterRequestDTO request) {
        if (usersRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("Este correo ya está en uso");
        }

        Users user = new Users();
        user.setNombres(request.getNombres());
        user.setApellidos(request.getApellidos());
        user.setCorreo(request.getCorreo());
        user.setTelefono(request.getTelefono());
        user.setContrasena(passwordEncoder.encode(request.getContrasena()));
        user.setRol(request.getRol());
        user.setEstado(request.getEstado());
        usersRepository.save(user);

        // Corregido: MessageResponseDTO usualmente no tiene constructor con String si falla el build
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Registro exitoso");
        return response;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        Users user = usersRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getContrasena(), user.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String jwt = jwtService.generateToken(user.getCorreo(), user.getRol());
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

    public void enviarSolicitudRecuperacion(ForgotPasswordRequestDTO request) {
        var usuario = usersRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("No se encontró un usuario con ese correo"));
    
        String token = jwtService.generateResetToken(usuario.getCorreo());
        emailService.enviarCorreoRecuperacion(usuario.getCorreo(), token);
    }
    public MessageResponseDTO verifyRecoveryCode(VerifyCodeRequestDTO request) {
        if (!passwordResetCodeService.isValidCode(request.getCorreo(), request.getCodigo())) {
            throw new RuntimeException("Código inválido o expirado");
        }
    
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Código verificado correctamente");
        return response;
    }
}