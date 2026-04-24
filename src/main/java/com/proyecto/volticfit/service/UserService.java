package com.proyecto.volticfit.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.UserRequestDTO;
import com.proyecto.volticfit.dto.UserResponseDTO;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    // Guardar usuario
    public MessageResponseDTO save(UserRequestDTO request) {

        if (usersRepository.findByemail(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está en uso");
        }

        Users user = new Users();
        user.setNombres(request.getNombres());
        user.setApellidos(request.getApellidos());
        user.setCorreo(request.getCorreo());
        user.setTelefono(request.getTelefono());
        user.setContrasena(passwordEncoder.encode(request.getContrasena()));

        usersRepository.save(user);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Usuario creado correctamente");

        return response;
    }

    // Obtener todos los usuarios (sin mapper separado)
    public List<UserResponseDTO> findAll() {

        List<Users> users = usersRepository.findAll();
        List<UserResponseDTO> response = new ArrayList<>();

        for (Users user : users) {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId_usuario(user.getId_usuario());
            dto.setApellidos(user.getApellidos());
            dto.setCorreo(user.getCorreo());
            dto.setTelefono(user.getTelefono());
            response.add(dto);
        }

        return response;
    }
}