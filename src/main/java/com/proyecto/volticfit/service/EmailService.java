package com.proyecto.volticfit.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import com.proyecto.volticfit.config.AppConstants;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio encargado de la logica de envio de correos electronicos.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendRecoveryCode(String destinatario, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            prepareTransactionalMessage(message, helper);
            helper.setSubject(AppConstants.RECOVERY_SUBJECT);

            String html = String.format(AppConstants.RECOVERY_HTML_TEMPLATE, token, AppConstants.RECOVERY_EXPIRATION_MINUTES);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Recovery code sent successfully to: {}", destinatario);
        } catch (Exception e) {
            log.error("Failed to send recovery code to {}: {}", destinatario, e.getMessage());
        }
    }

    @Async
    public void sendPasswordChangedNotification(String destinatario, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            prepareTransactionalMessage(message, helper);
            helper.setSubject("Tu contrasena de VolticFit fue actualizada");

            String safeName = userName == null || userName.isBlank() ? "usuario" : userName.trim();
            String html = """
                    <div style="font-family:Arial,Helvetica,sans-serif;background:#1e1e1e;color:#ffffff;padding:28px;">
                      <div style="max-width:560px;margin:auto;background:#272727;border:1px solid #3a3a3a;border-radius:10px;padding:24px;">
                        <h2 style="margin:0 0 12px;color:#f5a800;">Contrasena actualizada</h2>
                        <p style="margin:0 0 12px;color:#ffffff;">Hola %s,</p>
                        <p style="margin:0 0 16px;color:#d1d5db;">Te confirmamos que la contrasena de tu cuenta VolticFit fue modificada correctamente.</p>
                        <p style="margin:0;color:#aaaaaa;font-size:13px;">Si no realizaste este cambio, comunicate de inmediato con un administrador del gimnasio.</p>
                      </div>
                    </div>
                    """.formatted(safeName);

            helper.setText(html, true);
            mailSender.send(message);
            log.info("Password change notification sent successfully to: {}", destinatario);
        } catch (Exception e) {
            log.error("Failed to send password change notification to {}: {}", destinatario, e.getMessage());
        }
    }

    private void prepareTransactionalMessage(MimeMessage message, MimeMessageHelper helper) throws Exception {
        helper.setFrom(new InternetAddress(AppConstants.MAIL_FROM, "VolticFit", "UTF-8"));
        helper.setReplyTo(AppConstants.MAIL_FROM);
        message.addHeader("X-Mailer", "VolticFit Mailer");
        message.addHeader("Precedence", "transactional");
        message.addHeader("Auto-Submitted", "auto-generated");
        message.addHeader("List-Unsubscribe", "<mailto:" + AppConstants.MAIL_FROM + ">");
    }
}
