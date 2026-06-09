package com.proyecto.volticfit.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Users.UpdateUserRoleDTO;
import com.proyecto.volticfit.dto.Users.UpdateUserStateDTO;
import com.proyecto.volticfit.dto.Users.UpdateUserDTO;
import com.proyecto.volticfit.entity.Role;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.RoleRepository;
import com.proyecto.volticfit.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing users.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    // Repositorio de usuarios para acceder a los datos de los usuarios en la base de datos
    private final UsersRepository usersRepository;

    // Repositorio de roles para acceder a los datos de los roles en la base de datos
    private final RoleRepository roleRepository;
    
    // Inyectar el codificador de contraseñas para manejar el hashing de las contraseñas de los usuarios
    private final PasswordEncoder passwordEncoder;

    public MessageResponseDTO updateUser(Long id, UpdateUserDTO request, String requesterRole, Long requesterId) {

        if (!"admin".equalsIgnoreCase(requesterRole) && !requesterId.equals(id)) {
            throw new RuntimeException("No tienes permiso para actualizar este usuario");
        }

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Optional<Users> existing = usersRepository.findByEmail(request.getEmail());
            if (existing.isPresent()) {
                throw new RuntimeException("Este correo ya esta registrado");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getNames() != null)
            user.setNames(request.getNames());
        if (request.getSurnames() != null)
            user.setSurnames(request.getSurnames());
        if (request.getDocType() != null)
            user.setDocType(request.getDocType());
        if (request.getDocNumber() != null)
            user.setDocNum(request.getDocNumber());
        if (request.getPhone() != null)
            user.setPhone(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        usersRepository.save(user);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Usuario actualizado correctamente");
        return response;
    }

    public MessageResponseDTO updateUserRole(Long id, UpdateUserRoleDTO request, String requesterRole) {
        if (!"admin".equalsIgnoreCase(requesterRole)) {
            throw new RuntimeException("Solo un administrador puede cambiar el rol");
        }

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));

        Role role = roleRepository.findByName(request.getRole().trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("No se encontro el rol seleccionado"));

        user.setRole(role);
        usersRepository.save(user);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Rol actualizado correctamente");
        return response;
    }

    public MessageResponseDTO updateUserState(Long id, UpdateUserStateDTO request, String requesterRole) {
        if (!"admin".equalsIgnoreCase(requesterRole)) {
            throw new RuntimeException("Solo un administrador puede cambiar el estado");
        }

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));

        user.setState(request.getState());
        usersRepository.save(user);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Estado actualizado correctamente");
        return response;
    }

    public MessageResponseDTO deactivateAccount(Long id, String requesterRole, Long requesterId) {
        if (!"admin".equalsIgnoreCase(requesterRole) && !requesterId.equals(id)) {
            throw new RuntimeException("No tienes permiso para inactivar esta cuenta");
        }

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el usuario"));

        user.setState(false);
        usersRepository.save(user);

        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Cuenta inactivada correctamente");
        return response;
    }

}