package com.proyecto.volticfit.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Properties;

/**
 * Servicio de envío de correos usando Gmail API con autenticación OAuth2 (Service Account).
 * Evita las restricciones de SMTP en entornos cloud como Railway.
 */
@Service
@Log4j2
public class GmailService {

    private static final String GMAIL_SEND_SCOPE = "https://www.googleapis.com/auth/gmail.send";
    private static final String APPLICATION_NAME = "VolticFit";

    @Value("${GMAIL_CREDENTIALS_JSON:}")
    private String credentialsJson;

    @Value("${spring.mail.username:volticfit@gmail.com}")
    private String senderEmail;

    /**
     * Envía un correo HTML usando Gmail API con credenciales de Service Account.
     *
     * @param to          dirección de destino
     * @param subject     asunto del correo
     * @param htmlContent cuerpo HTML del correo
     * @return true si el envío fue exitoso, false en caso contrario
     */
    public boolean sendEmail(String to, String subject, String htmlContent) {
        if (credentialsJson == null || credentialsJson.isBlank()) {
            log.warn("[GmailService] GMAIL_CREDENTIALS_JSON no está configurado. Omitiendo envío vía Gmail API.");
            return false;
        }

        try {
            GoogleCredentials credentials = buildCredentials();
            Gmail gmail = buildGmailClient(credentials);
            Message message = buildMimeMessage(to, subject, htmlContent);

            gmail.users().messages().send("me", message).execute();
            log.info("[GmailService] Correo enviado exitosamente a {} vía Gmail API.", to);
            return true;

        } catch (Exception e) {
            log.error("[GmailService] Error al enviar correo a {} vía Gmail API: {}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Construye las credenciales OAuth2 a partir del JSON de Service Account
     * almacenado en la variable de entorno GMAIL_CREDENTIALS_JSON.
     */
    private GoogleCredentials buildCredentials() throws Exception {
        byte[] credBytes = credentialsJson.getBytes(StandardCharsets.UTF_8);
        GoogleCredentials credentials = ServiceAccountCredentials
                .fromStream(new ByteArrayInputStream(credBytes))
                .createScoped(Collections.singletonList(GMAIL_SEND_SCOPE));

        // Si el service account tiene delegación de dominio, impersonar al remitente
        if (credentials instanceof ServiceAccountCredentials serviceAccountCreds) {
            credentials = serviceAccountCreds.createDelegated(senderEmail);
        }

        return credentials;
    }

    /**
     * Construye el cliente de Gmail API autenticado.
     */
    private Gmail buildGmailClient(GoogleCredentials credentials) throws Exception {
        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Construye el mensaje MIME con soporte HTML y headers transaccionales,
     * codificado en Base64 URL-safe para la Gmail API.
     */
    private Message buildMimeMessage(String to, String subject, String htmlContent) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(senderEmail, "VolticFit", "UTF-8"));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject, "UTF-8");
        email.addHeader("X-Mailer", "VolticFit Mailer");
        email.addHeader("Precedence", "transactional");
        email.addHeader("Auto-Submitted", "auto-generated");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");

        MimeMultipart multipart = new MimeMultipart("alternative");
        multipart.addBodyPart(htmlPart);
        email.setContent(multipart);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);

        String encodedEmail = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(buffer.toByteArray());

        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}
