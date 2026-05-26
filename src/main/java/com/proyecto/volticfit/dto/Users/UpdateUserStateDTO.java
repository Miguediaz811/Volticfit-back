package com.proyecto.volticfit.dto.Users;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserStateDTO {

    @NotNull(message = "State is required")
    private Boolean state;
}