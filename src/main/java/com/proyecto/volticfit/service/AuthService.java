package com.proyecto.volticfit.service;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.ChangePasswordRequestDTO;
import com.proyecto.volticfit.dto.LoginRequestDTO;
import com.proyecto.volticfit.dto.LoginResponseDTO;
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.RefreshTokenResponseDTO;
import com.proyecto.volticfit.dto.RegisterRequestDTO;
import com.proyecto.volticfit.dto.RestorePasswordRequestDTO; // <--- Importante añadirlo
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Map;

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
        user.setTipo_doc(request.getTipo_doc());
        user.setNum_doc(request.getNum_doc());
        user.setCorreo(request.getCorreo());
        user.setTelefono(request.getTelefono());
        user.setContrasena(passwordEncoder.encode(request.getContrasena()));
        user.setRol(request.getRol());
        user.setEstado(request.getEstado());
        usersRepository.save(user);
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
/* 
    public RefreshTokenResponseDTO refreshToken(String token) {
        String jwt = jwtService.refreshToken(token);
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO();
        response.setJwt(jwt);
        return response;
    }
*/
    @Transactional
    public MessageResponseDTO changePassword(Long userId, ChangePasswordRequestDTO request) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getContrasena())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        user.setContrasena(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Contraseña actualizada correctamente");
        return response;
    }

    /**
     * REEMPLAZO: Este método ahora sigue el estilo de Manada para el reset
     */
    @Transactional
    public MessageResponseDTO restorePassword(RestorePasswordRequestDTO request) {
        Users user = usersRepository.findByCorreo(request.getCorreo())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        user.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usersRepository.save(user);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Contraseña actualizada con éxito");
        return response;
    }
}