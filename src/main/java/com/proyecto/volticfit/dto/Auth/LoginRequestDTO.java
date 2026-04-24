package com.proyecto.volticfit.dto.Auth;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}