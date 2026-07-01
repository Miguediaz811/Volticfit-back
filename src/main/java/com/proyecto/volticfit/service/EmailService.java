package com.proyecto.volticfit.service;

import com.proyecto.volticfit.config.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Servicio encargado del envio de correos electronicos.
 * Primero intenta Gmail API y, si existe API key, puede usar Resend como respaldo HTTP.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final GmailService gmailService;

    @Value("${gmail.api.enabled:false}")
    private boolean gmailApiEnabled;

    @Value("${RESEND_API_KEY:}")
    private String resendApiKey;

    @Value("${RESEND_FROM_EMAIL:VolticFit <onboarding@resend.dev>}")
    private String resendFromEmail;

    @Async
    public void sendRecoveryCode(String destinatario, String token) {
        log.info("[EmailService] Iniciando envio asincrono de codigo de recuperacion para: {}", destinatario);
        String subject = AppConstants.RECOVERY_SUBJECT;
        String html = String.format(AppConstants.RECOVERY_HTML_TEMPLATE, token, AppConstants.RECOVERY_EXPIRATION_MINUTES);
        sendEmail(destinatario, subject, html);
    }

    @Async
    public void sendPasswordChangedNotification(String destinatario, String userName) {
        log.info("[EmailService] Iniciando envio asincrono de notificacion de cambio de contrasena para: {}", destinatario);
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

    private void sendEmail(String to, String subject, String htmlContent) {
        if (gmailApiEnabled) {
            log.info("[EmailService] Proveedor seleccionado: Gmail API");
            boolean sent = gmailService.sendEmail(to, subject, htmlContent);
            if (sent) {
                return;
            }
            log.warn("[EmailService] Gmail API fallo. Intentando proveedor alternativo HTTP/API...");
        }

        if (resendApiKey != null && !resendApiKey.isBlank()) {
            log.info("[EmailService] Proveedor seleccionado: Resend API");
            sendEmailViaResend(to, subject, htmlContent);
            return;
        }

        log.error("[EmailService] No se envio el correo a {} porque no hay proveedor configurado. Activa Gmail API o configura Resend.", to);
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

            log.info("[EmailService] Enviando correo a {} via Resend API...", to);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                log.info("[EmailService] Correo enviado exitosamente a {} via Resend API. Codigo: {}", to, response.statusCode());
            } else {
                log.error("[EmailService] Fallo al enviar correo via Resend API. Codigo: {}. Respuesta: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("[EmailService] Error al enviar correo via Resend API a {}: {}", to, e.getMessage(), e);
        }
    }

    private String escapeJson(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
