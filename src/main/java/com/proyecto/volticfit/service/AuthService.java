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

    public MessageResponseDTO register(RegisterRequestDTO request) {
        if (usersRepository.findByemail(request.getCorreo()).isPresent()) {
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

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Registro exitoso");
        return response;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        Optional<Users> userOpt = usersRepository.findByemail(request.getCorreo());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Users user = userOpt.get();

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
     * Refresca el token JWT del usuario.
     * @param token JWT viejo (puede estar expirado)
     * @return Nuevo JWT
     */
    public RefreshTokenResponseDTO refreshToken(String token) {
        String jwt = jwtService.refreshToken(token);
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO();
        response.setJwt(jwt);
        return response;
    }

    @Transactional
    public MessageResponseDTO changePassword(Long userId, ChangePasswordRequestDTO request) {

    if (userId == null) {
        throw new RuntimeException("Usuario no autenticado");
    }

    Users user = usersRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // validar contraseña actual
    if (!user.getContrasena().equals(request.getCurrentPassword())) {
        throw new RuntimeException("La contraseña actual es incorrecta");
    }

    // actualizar contraseña
    user.setContrasena(passwordEncoder.encode(request.getNewPassword()));
    usersRepository.save(user);

    MessageResponseDTO response = new MessageResponseDTO();
    response.setMessage("Contraseña actualizada correctamente");

    return response;
}
}
