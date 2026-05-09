package com.proyecto.volticfit.service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Auth.ForgotPasswordRequestDTO;
import com.proyecto.volticfit.dto.Auth.RestorePasswordRequestDTO;
import com.proyecto.volticfit.dto.Auth.VerifyCodeRequestDTO;
import com.proyecto.volticfit.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para gestionar el ciclo de vida de la recuperación de contraseñas
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    /**
     * Genera un código aleatorio y lo envía por correo si el usuario existe
    
     */
    public MessageResponseDTO forgotPassword(ForgotPasswordRequestDTO request) {
        return usersRepository.findByEmail(request.getEmail())
            .map(user -> {
                String code = String.format("%06d", new Random().nextInt(999999));
                verificationCodes.put(request.getEmail(), code);
                emailService.sendRecoveryCode(request.getEmail(), code);
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("Código de recuperación enviado");
                return response;
            })
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese correo"));
    }

    /**
     * Verifica que el código ingresado coincida con el enviado al correo
    
     */
    public MessageResponseDTO verifyCode(VerifyCodeRequestDTO request) {
        String storedCode = verificationCodes.get(request.getEmail());
        if (storedCode != null && storedCode.equals(request.getCode())) {
            MessageResponseDTO response = new MessageResponseDTO();
            response.setMessage("Código verificado correctamente");
            return response;
        }
        throw new RuntimeException("Código inválido o expirado");
    }

    /**
     * Actualiza la contraseña en la base de datos y elimina el código usado
     
     */
    public MessageResponseDTO restorePassword(RestorePasswordRequestDTO request) {
        String storedCode = verificationCodes.get(request.getEmail());
        if (storedCode == null || !storedCode.equals(request.getCode())) {
            throw new RuntimeException("El código no es válido para esta operación");
        }

        return usersRepository.findByEmail(request.getEmail())
            .map(user -> {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                usersRepository.save(user);
                verificationCodes.remove(request.getEmail());
                MessageResponseDTO response = new MessageResponseDTO();
                response.setMessage("Contraseña restaurada exitosamente");
                return response;
            })
            .orElseThrow(() -> new RuntimeException("Error al identificar al usuario"));
    }
}