package com.proyecto.volticfit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Users.UpdateUserDTO;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.UsersRepository;
import com.proyecto.volticfit.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UsersRepository usersRepository;

    /**
     * Devuelve los datos del usuario autenticado.
     * El JwtValidationFilter ya resolvió el userId desde el token y lo dejó en el request.
     */
    @Operation(summary = "Get authenticated user profile",
        responses = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                content = @Content(schema = @Schema(implementation = Users.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    @GetMapping("/me")
    public ResponseEntity<Users> getMe(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Actualizar datos de usuario
     * 
     * @param id ID del usuario a actualizar
     * @param request datos a actualizar
     * @param httpRequest datos de la solicitud para obtener el rol y userId del usuario que realiza la solicitud
     * @return MessageResponseDTO con el mensaje de éxito o error de la actualización del usuario
     */
    
    @Operation(summary = "Update user data - ADMIN or same user",
        responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "Error updating user")
        }
    )

    
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO request,
            HttpServletRequest httpRequest) {
        try {
            String role       = (String) httpRequest.getAttribute("role");
            Long requesterId  = (Long)   httpRequest.getAttribute("userId");
            return ResponseEntity.ok(userService.updateUser(id, request, role, requesterId));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Inactivar cuenta de usuario
     * 
     * @param id ID del usuario a inactivar
     * @return MessageResponseDTO con el mensaje de éxito o error de la inactivación de la cuenta
     */
    @Operation(summary = "Deactivate user account - ADMIN or same user",
        responses = {
            @ApiResponse(responseCode = "200", description = "Account deactivated successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "User not found")
        }
    )
    @PutMapping("/{id}/inactivar")
    public ResponseEntity<MessageResponseDTO> deactivateAccount(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            String role      = (String) httpRequest.getAttribute("role");
            Long requesterId = (Long)   httpRequest.getAttribute("userId");
            return ResponseEntity.ok(userService.deactivateAccount(id, role, requesterId));
        } catch (RuntimeException e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            MessageResponseDTO error = new MessageResponseDTO();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}