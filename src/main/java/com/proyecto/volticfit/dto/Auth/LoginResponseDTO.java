package com.proyecto.volticfit.dto.Auth;

import com.proyecto.volticfit.dto.MessageResponseDTO;

import lombok.Data;

@Data
public class LoginResponseDTO extends MessageResponseDTO {
    private String jwt;
}