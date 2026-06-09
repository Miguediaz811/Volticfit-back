package com.proyecto.volticfit.dto.Support;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupportReplyDTO {
    @NotBlank(message = "La respuesta no puede estar vacia")
    private String message;
}
