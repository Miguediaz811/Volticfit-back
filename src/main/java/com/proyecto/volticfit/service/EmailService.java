package com.proyecto.volticfit.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.proyecto.volticfit.config.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio encargado de la lógica de envío de correos electrónicos.
 * Se comunica con el servidor SMTP configurado para notificar a los usuarios.
 * * @author Mateo
 * @version 1.0
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    /**
     * Bean de Spring para el envío de mensajes de correo simple.
     */
    private final JavaMailSender mailSender;

    /**
     * Construye y envía un correo electrónico para la restauración de credenciales.
     * * @param destinatario El correo electrónico del usuario.
     * @param token El identificador único para validar el cambio de clave.
     */
    public void enviarCorreoRecuperacion(String destinatario, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatario);
            message.setFrom(AppConstants.MAIL_FROM);
            message.setSubject(AppConstants.RECOVERY_SUBJECT);
            
            // Genera el cuerpo del mensaje usando las constantes del sistema
            String cuerpo = String.format(
                AppConstants.RECOVERY_BODY_TEMPLATE, 
                AppConstants.FRONTEND_URL, 
                token
            );
            
            message.setText(cuerpo);
            mailSender.send(message);
            
            log.info("📧 Correo de recuperación enviado con éxito a: {}", destinatario);
            
        } catch (Exception e) {
            log.error("❌ Error crítico al enviar correo a {}: {}", destinatario, e.getMessage());
        }
    }
}