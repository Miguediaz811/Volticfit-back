package com.proyecto.volticfit.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.proyecto.volticfit.dto.Notification.NotificationRequest;
import com.proyecto.volticfit.exception.NotificationException;
import com.proyecto.volticfit.model.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService {

    private final EmailService emailService;

    /*
        Historial temporal
     */
    private final List<Notification> notifications =
            new ArrayList<>();

    /*
        Generador de IDs
     */
    private final AtomicLong counter =
            new AtomicLong();

    public Notification send(
            NotificationRequest request
    ) {

        validate(request);

        Notification notification =
                new Notification();

        notification.setId(
                counter.incrementAndGet()
        );

        notification.setTitulo(
                request.getTitulo()
        );

        notification.setMensaje(
                request.getMensaje()
        );

        notification.setDestinatario(
                request.getDestinatario()
        );

        notification.setFechaCreacion(
                LocalDateTime.now()
        );

        notification.setEstado(false);

        notification.setReintentos(0);

        try {

            emailService.sendNotificationEmail(
                    request.getDestinatario(),
                    request.getTitulo(),
                    request.getMensaje()
            );

            notification.setEstado(true);

            log.info(
                    "Notification sent successfully"
            );

        } catch (Exception e) {

            notification.setEstado(false);

            log.error(
                    "Error sending notification: {}",
                    e.getMessage()
            );

            throw new NotificationException(
                    "Error sending notification"
            );
        }

        notifications.add(notification);

        return notification;
    }

    public List<Notification> getAll() {

        return notifications;
    }

    private void validate(
            NotificationRequest request
    ) {

        if (request.getTitulo() == null
                || request.getTitulo().isBlank()) {

            throw new NotificationException(
                    "El título es obligatorio"
            );
        }

        if (request.getMensaje() == null
                || request.getMensaje().isBlank()) {

            throw new NotificationException(
                    "El mensaje es obligatorio"
            );
        }

        if (request.getDestinatario() == null
                || request.getDestinatario().isBlank()) {

            throw new NotificationException(
                    "El destinatario es obligatorio"
            );
        }
    }
}