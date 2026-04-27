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

@Service
@RequiredArgsConstructor
public class AuthService {

    // Inyectamos el PasswordEncoder para manejar el hashing de contraseñas
    private final PasswordEncoder passwordEncoder;

    // Inyectamos el UsersRepository para acceder a los datos de usuarios
    private final UsersRepository usersRepository;

    // Inyectamos el JwtService para manejar la generación y validación de tokens JWT
    private final JwtService jwtService;

    private final EmailService emailService;
    private final PasswordResetService passwordResetService;
    
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
     * Este método se encarga de actualizar la contraseña sin necesidad de conocer la actual,
     *  ideal para casos de recuperación de contraseña. Se asume que el proceso de verificación 
     * (como un token enviado por email) se ha manejado antes de llamar a este método.
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

        public MessageResponseDTO verifyRecoveryCode(ForgotPasswordRequestDTO request) {
        usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
 
        jwtService.generateResetToken(request.getEmail());
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("If the email is registered, you will receive a recovery link.");
        return response;
    }
 
    public MessageResponseDTO verifyRecoveryCode(VerifyCodeRequestDTO request) {
        if (!jwtService.isTokenValid(request.getToken())) {
            throw new RuntimeException("Invalid or expired recovery token");
        }
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Token verified successfully");
        return response;
    }

    //Listar usuarios activos
    public List<Users> getAllUsers() {
        return usersRepository.findByStateTrue();
    }
}