package com.proyecto.volticfit.dto.Auth;

import lombok.Data;

@Data
public class RestorePasswordRequestDTO {
    private String email;
    private String code;
    private String newPassword;
}