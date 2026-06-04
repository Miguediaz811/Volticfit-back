package com.proyecto.volticfit.dto.Notification;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationRequestDTO {

    private String titulo;

    private String mensaje;

    private String tipo;

    private Long usuarioDestinoId;
    
    private LocalDateTime fechaExpiracion;
}