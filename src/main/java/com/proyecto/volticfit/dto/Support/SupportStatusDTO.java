package com.proyecto.volticfit.dto.Support;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupportStatusDTO {
    @NotBlank(message = "El estado es obligatorio")
    private String status;
}
