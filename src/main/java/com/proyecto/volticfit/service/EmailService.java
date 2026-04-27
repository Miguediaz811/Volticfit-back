package com.proyecto.volticfit.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado del envío de correos electrónicos para la plataforma
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Envía un correo con el código de recuperación generado al usuario
     */
    public void sendRecoveryCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Código de Recuperación - VolticFit");
        message.setText("Tu código de recuperación es: " + code + "\n\nEste código es necesario para restaurar tu contraseña.");
        mailSender.send(message);
    }
}