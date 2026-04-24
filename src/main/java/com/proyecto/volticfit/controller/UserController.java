package com.proyecto.volticfit.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.UserRequestDTO;
import com.proyecto.volticfit.dto.UserResponseDTO;
import com.proyecto.volticfit.enums.RoleEnum;
import com.proyecto.volticfit.security.RequiresRole;
import com.proyecto.volticfit.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 
     * @param request
     * @return
     */
    @PostMapping
    @RequiresRole({ RoleEnum.ADMIN })
    public ResponseEntity<MessageResponseDTO> save(@RequestBody UserRequestDTO request) {
        try {
            MessageResponseDTO response = userService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 
     * @return
     */
    @GetMapping
    @RequiresRole({ RoleEnum.USER, RoleEnum.ADMIN })
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        try {
            List<UserResponseDTO> response = userService.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
