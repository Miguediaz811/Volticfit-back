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
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordResetCodeService passwordResetCodeService; 

    /**
     * Registra un nuevo usuario en la base de datos encriptando la contraseña.
     */
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
        // Encriptación manual usando el Bean definido en AppConfig
        user.setContrasena(passwordEncoder.encode(request.getContrasena()));
        user.setRol(request.getRol());
        user.setEstado(request.getEstado());
        
        usersRepository.save(user);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Registro exitoso");
        return response;
    }

    /**
     * Valida credenciales y genera un JWT.
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        Users user = usersRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Comparación segura de hashes
        if (!passwordEncoder.matches(request.getContrasena(), user.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String jwt = jwtService.generateToken(user.getCorreo(), user.getRol());
        
        LoginResponseDTO response = new LoginResponseDTO();
        response.setMessage("Inicio de sesión exitoso");
        response.setJwt(jwt);
        return response;
    }

    /**
     * Refresca un token existente.
     */
    public RefreshTokenResponseDTO refreshToken(String token) {
        String jwt = jwtService.refreshToken(token);
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO();
        response.setJwt(jwt);
        return response;
    }

    /**
     * Cambia la contraseña de un usuario validando la actual.
     */
    @Transactional
    public MessageResponseDTO changePassword(Long userId, ChangePasswordRequestDTO request) {
        if (userId == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // CORRECCIÓN: Se debe usar .matches para comparar contra el hash de la BD
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getContrasena())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // Encriptar la nueva contraseña antes de guardar
        user.setContrasena(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Contraseña actualizada correctamente");

        return response;
    }

    /**
     * Lógica para actualizar la contraseña desde el flujo de "Olvide mi contraseña" (sin login previo)
     */
    @Transactional
    public void updatePasswordFromReset(String correo, String nuevaPassword) {
        Users user = usersRepository.findByCorreo(correo)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        user.setContrasena(passwordEncoder.encode(nuevaPassword));
        usersRepository.save(user);
    }
}