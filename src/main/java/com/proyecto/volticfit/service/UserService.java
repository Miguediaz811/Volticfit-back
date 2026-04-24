package com.proyecto.volticfit.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.UpdateUserDTO;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
 
    public MessageResponseDTO updateUser(Long id, UpdateUserDTO request, String requesterRole, Long requesterId) {
 
        if (!"admin".equalsIgnoreCase(requesterRole) && !requesterId.equals(id)) {
            throw new RuntimeException("You do not have permission to update this user");
        }
 
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
 
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Optional<Users> existing = usersRepository.findByEmail(request.getEmail());
            if (existing.isPresent()) {
                throw new RuntimeException("This email is already in use by another user");
            }
            user.setEmail(request.getEmail());
        }
 
        if (request.getNames() != null) user.setNames(request.getNames());
        if (request.getSurnames() != null) user.setSurnames(request.getSurnames());
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
 
        usersRepository.save(user);
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("User updated successfully");
        return response;
    }

        public MessageResponseDTO deactivateAccount(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
 
        user.setState(false);
        usersRepository.save(user);
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Account deactivated successfully");
        return response;
    }

}
