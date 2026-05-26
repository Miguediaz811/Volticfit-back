package com.proyecto.volticfit.dto.Users;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRoleDTO {

    @NotBlank(message = "Role is required")
    private String role;
}