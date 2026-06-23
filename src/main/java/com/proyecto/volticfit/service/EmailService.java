package com.proyecto.volticfit.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

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
 * Servicio encargado de la logica de envio de correos electronicos (Soporta Resend API y Gmail SMTP de forma hibrida).
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private static final int SMTP_MAX_ATTEMPTS = 3;
    private static final long SMTP_BACKOFF_BASE_MS = 2000L;

    private final JavaMailSender mailSender;

    @Value("${RESEND_API_KEY:}")
    private String resendApiKey;

    @Value("${RESEND_FROM_EMAIL:VolticFit <onboarding@resend.dev>}")
    private String resendFromEmail;

    @Async
    public void sendRecoveryCode(String destinatario, String token) {
        log.info("[EmailService] Iniciando proceso de envío asíncrono de código de recuperación para: {}", destinatario);
        String subject = AppConstants.RECOVERY_SUBJECT;
        String html = String.format(AppConstants.RECOVERY_HTML_TEMPLATE, token, AppConstants.RECOVERY_EXPIRATION_MINUTES);

        if (resendApiKey != null && !resendApiKey.isBlank()) {
            log.info("[EmailService] Proveedor seleccionado: Resend API");
            sendEmailViaResend(destinatario, subject, html);
        } else {
            log.info("[EmailService] Proveedor seleccionado: SMTP (Gmail). Host: smtp.gmail.com:587");
            try {
                sendEmailViaJavaMailWithRetry(destinatario, subject, html);
            } catch (RuntimeException e) {
                log.error("[EmailService] Envío asíncrono de código de recuperación fallido definitivamente para {}: {}",
                        destinatario, e.getMessage());
            }
        }
    }

    @Async
    public void sendPasswordChangedNotification(String destinatario, String userName) {
        log.info("[EmailService] Iniciando proceso de envío asíncrono de notificación de cambio de contraseña para: {}", destinatario);
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

        if (resendApiKey != null && !resendApiKey.isBlank()) {
            log.info("[EmailService] Proveedor seleccionado: Resend API");
            sendEmailViaResend(destinatario, subject, html);
        } else {
            log.info("[EmailService] Proveedor seleccionado: SMTP (Gmail). Host: smtp.gmail.com:587");
            try {
                sendEmailViaJavaMailWithRetry(destinatario, subject, html);
            } catch (RuntimeException e) {
                log.error("[EmailService] Envío asíncrono de notificación de cambio de contraseña fallido definitivamente para {}: {}",
                        destinatario, e.getMessage());
            }
        }
    }

    /**
     * Sends an email via JavaMail with up to {@value #SMTP_MAX_ATTEMPTS} attempts
     * and exponential backoff between retries.
     *
     * <p>When called from an {@code @Async} context the exception is silently
     * discarded by the async executor (expected behaviour). When called
     * synchronously — e.g. from {@code EmailTestController} — the exception
     * propagates to the caller so it can surface a meaningful HTTP error.</p>
     *
     * @throws RuntimeException wrapping the last SMTP failure after all retries
     */
    public void sendEmailViaJavaMailWithRetry(String to, String subject, String htmlContent) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= SMTP_MAX_ATTEMPTS; attempt++) {
            log.info("[EmailService] SMTP intento {}/{} para: {}", attempt, SMTP_MAX_ATTEMPTS, to);
            try {
                sendEmailViaJavaMail(to, subject, htmlContent);
                return; // success — exit immediately
            } catch (Exception e) {
                lastException = e;
                log.warn("[EmailService] SMTP intento {}/{} fallido para {}. Causa: {} — {}",
                        attempt, SMTP_MAX_ATTEMPTS, to,
                        e.getClass().getSimpleName(), e.getMessage());

                if (attempt < SMTP_MAX_ATTEMPTS) {
                    long backoffMs = SMTP_BACKOFF_BASE_MS * (1L << (attempt - 1)); // 2s, 4s
                    log.info("[EmailService] Esperando {}ms antes del siguiente intento...", backoffMs);
                    try {
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.warn("[EmailService] Hilo interrumpido durante backoff. Abortando reintentos.");
                        break;
                    }
                }
            }
        }

        String errorDetail = lastException != null
                ? lastException.getClass().getSimpleName() + " — " + lastException.getMessage()
                : "error desconocido";
        log.error("[EmailService] Todos los intentos SMTP fallaron para {}. Último error: {}",
                to, errorDetail, lastException);
        throw new RuntimeException("SMTP falló tras " + SMTP_MAX_ATTEMPTS + " intentos: " + errorDetail, lastException);
    }

    private void sendEmailViaJavaMail(String to, String subject, String htmlContent) throws Exception {
        log.debug("[EmailService] Creando MimeMessage para: {}", to);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        prepareTransactionalMessage(message, helper);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        log.info("[EmailService] Estableciendo conexión SMTP con smtp.gmail.com:587 (STARTTLS)...");
        mailSender.send(message);
        log.info("[EmailService] Correo enviado exitosamente a {} vía SMTP.", to);
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
            log.debug("[EmailService] Construyendo payload JSON para Resend API. Destinatario: {}", to);
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

            log.info("[EmailService] Conectando a api.resend.com (HTTPS/443)...");
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                log.info("[EmailService] Correo enviado exitosamente a {} vía Resend API. HTTP {}", to, response.statusCode());
            } else {
                log.error("[EmailService] Resend API rechazó el envío. HTTP {}. Cuerpo de respuesta: {}",
                        response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("[EmailService] Error de conexión con Resend API para {}: {} — {}",
                    to, e.getClass().getSimpleName(), e.getMessage(), e);
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
