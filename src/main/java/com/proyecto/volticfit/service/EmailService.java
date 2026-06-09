package com.proyecto.volticfit.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.proyecto.volticfit.config.AppConstants;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio encargado de la lógica de envío de correos electrónicos.
 * Se comunica con el servidor SMTP configurado para notificar a los usuarios.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Construye y envía un correo HTML profesional para la recuperación de contraseña.
     *
     * @param destinatario El correo electrónico del usuario.
     * @param token        El código de 6 dígitos de verificación.
     */
    public void sendRecoveryCode(String destinatario, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            // Nombre visible del remitente — evita que llegue como solo el correo crudo
            helper.setFrom(new InternetAddress(AppConstants.MAIL_FROM, "VolticFit", "UTF-8"));
            helper.setReplyTo(AppConstants.MAIL_FROM);
            helper.setSubject(AppConstants.RECOVERY_SUBJECT);

            // Encabezados anti-spam estándar
            message.addHeader("X-Mailer", "VolticFit Mailer");
            message.addHeader("Precedence", "transactional");
            message.addHeader("Auto-Submitted", "auto-generated");

            String html = String.format(AppConstants.RECOVERY_HTML_TEMPLATE, token, AppConstants.RECOVERY_EXPIRATION_MINUTES);
            helper.setText(html, true); // true = es HTML

            mailSender.send(message);
            log.info("Recovery code sent successfully to: {}", destinatario);

        } catch (Exception e) {
            log.error("Failed to send recovery code to {}: {}", destinatario, e.getMessage());
        }
    }
}