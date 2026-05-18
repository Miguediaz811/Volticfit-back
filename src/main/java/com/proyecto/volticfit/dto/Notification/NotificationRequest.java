package com.proyecto.volticfit.dto.Notification;

import lombok.Data;

@Data
public class NotificationRequest {

    private String titulo;

    private String mensaje;

    private String destinatario;
}