package com.proyecto.volticfit.dto.Support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSupportTicketDTO {
    @NotBlank(message = "El asunto es obligatorio")
    @Size(min = 4, message = "El asunto debe tener al menos 4 caracteres")
    private String subject;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(min = 12, max = 250, message = "La descripcion debe tener entre 12 y 250 caracteres")
    private String description;

    private String attachment;
}
