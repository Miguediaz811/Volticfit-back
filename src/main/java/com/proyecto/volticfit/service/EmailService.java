package com.proyecto.volticfit.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.proyecto.volticfit.config.AppConstants;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio encargado de la logica de envio de correos electronicos.
 * Soporta tres proveedores con prioridad decreciente:
 *   1. Gmail API (OAuth2) — si {@code gmail.api.enabled=true}
 *   2. Resend API          — si {@code RESEND_API_KEY} está configurado
 *   3. Gmail SMTP          — fallback
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final GmailService gmailService;

    @Value("${gmail.api.enabled:false}")
    private boolean gmailApiEnabled;

    @Value("${RESEND_API_KEY:}")
    private String resendApiKey;

    @Value("${RESEND_FROM_EMAIL:VolticFit <onboarding@resend.dev>}")
    private String resendFromEmail;

    @Async
    public void sendRecoveryCode(String destinatario, String token) {
        log.info("[EmailService] Iniciando proceso de envío asíncrono para: {}", destinatario);
        String subject = AppConstants.RECOVERY_SUBJECT;
        String html = String.format(AppConstants.RECOVERY_HTML_TEMPLATE, token, AppConstants.RECOVERY_EXPIRATION_MINUTES);
        sendEmail(destinatario, subject, html);
    }

    @Async
    public void sendPasswordChangedNotification(String destinatario, String userName) {
        log.info("[EmailService] Iniciando proceso de envío asíncrono para notificación de cambio de contraseña: {}", destinatario);
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
        String subject = "Tu contrasena de VolticFit fue actualizada";
        sendEmail(destinatario, subject, html);
    }

    // -------------------------------------------------------------------------
    // Routing interno de proveedores
    // -------------------------------------------------------------------------

    /**
     * Enruta el correo al proveedor disponible según la siguiente prioridad:
     * <ol>
     *   <li>Gmail API (OAuth2) si {@code gmail.api.enabled=true}</li>
     *   <li>Resend API si {@code RESEND_API_KEY} está configurado</li>
     *   <li>Gmail SMTP como fallback</li>
     * </ol>
     */
    private void sendEmail(String to, String subject, String htmlContent) {
        if (gmailApiEnabled) {
            log.info("[EmailService] Proveedor seleccionado: Gmail API (OAuth2)");
            boolean sent = gmailService.sendEmail(to, subject, htmlContent);
            if (sent) return;
            log.warn("[EmailService] Gmail API falló. Intentando proveedor alternativo...");
        }

        if (resendApiKey != null && !resendApiKey.isBlank()) {
            log.info("[EmailService] Proveedor seleccionado: Resend API");
            sendEmailViaResend(to, subject, htmlContent);
        } else {
            log.info("[EmailService] Proveedor seleccionado: Gmail SMTP");
            sendEmailViaJavaMail(to, subject, htmlContent);
        }
    }

    // -------------------------------------------------------------------------
    // Implementaciones de proveedores
    // -------------------------------------------------------------------------

    private void sendEmailViaJavaMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            prepareTransactionalMessage(message, helper);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            log.info("[EmailService] Enviando correo a {} vía SMTP (Gmail)...", to);
            mailSender.send(message);
            log.info("Correo enviado exitosamente a: {}", to);
        } catch (Exception e) {
            log.error("Fallo al enviar el correo a {} vía SMTP: {}", to, e.getMessage());
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

    private void sendEmailViaResend(String to, String subject, String htmlContent) {
        try {
            String jsonPayload = """
                {
                  "from": "%s",
                  "to": ["%s"],
                  "subject": "%s",
                  "html": "%s"
                }
                """.formatted(
                    escapeJson(resendFromEmail),
                    escapeJson(to),
                    escapeJson(subject),
                    escapeJson(htmlContent)
                );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            log.info("[EmailService] Enviando correo a {} vía Resend API (puerto 443)...", to);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                log.info("[EmailService] Correo enviado exitosamente a {} vía Resend API. Código: {}", to, response.statusCode());
            } else {
                log.error("[EmailService] Falló al enviar correo vía Resend API. Código: {}. Respuesta: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("[EmailService] Error al enviar correo vía Resend API a {}: {}", to, e.getMessage(), e);
        }
    }

    private String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "");
    }
}
