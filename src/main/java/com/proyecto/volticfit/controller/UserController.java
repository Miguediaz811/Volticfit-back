package com.proyecto.volticfit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Users.UpdateUserDTO;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
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
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    /**
     * servicio de usuarios
     */
    private final UserService userService;

    @Operation(summary = "Update user data - ADMIN or same user",
        responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "Error updating user")
        }
    )
 
    /**
     * Actualizar datos de usuario
     * 
     * @param id ID del usuario a actualizar
     * @param request datos a actualizar
     * @param httpRequest datos de la solicitud para obtener el rol y userId del usuario que realiza la solicitud
     * @return MessageResponseDTO con el mensaje de éxito o error de la actualización del usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO request,HttpServletRequest httpRequest) {
        try {
            String role = (String) httpRequest.getAttribute("role");
            Long requesterId = (Long) httpRequest.getAttribute("userId");
 
            MessageResponseDTO response = userService.updateUser(id, request, role, requesterId);
            return ResponseEntity.ok(response);
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

    @Operation(summary = "Deactivate user account - ADMIN only",
        responses = {
            @ApiResponse(responseCode = "200", description = "Account deactivated successfully",
                content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
        }
    )

    /**
     * Inactivar cuenta de usuario
     * 
     * @param id ID del usuario a inactivar
     * @return MessageResponseDTO con el mensaje de éxito o error de la inactivación de la cuenta
     */
    @PutMapping("/{id}/inactivar")
    public ResponseEntity<MessageResponseDTO> deactivateAccount(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            String role = (String) httpRequest.getAttribute("role");
            Long requesterId = (Long) httpRequest.getAttribute("userId");
 
            MessageResponseDTO response = userService.deactivateAccount(id, role, requesterId);
            return ResponseEntity.ok(response);
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
