package com.proyecto.volticfit.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.Properties;

/**
 * Servicio de envio de correos usando Gmail API con OAuth2 de usuario.
 * Este flujo funciona con cuentas Gmail normales usando client id, client secret y refresh token.
 */
@Service
@Log4j2
public class GmailService {

    private static final String GMAIL_SEND_SCOPE = "https://www.googleapis.com/auth/gmail.send";
    private static final String APPLICATION_NAME = "VolticFit";

    @Value("${GMAIL_CLIENT_ID:}")
    private String clientId;

    @Value("${GMAIL_CLIENT_SECRET:}")
    private String clientSecret;

    @Value("${GMAIL_REFRESH_TOKEN:}")
    private String refreshToken;

    @Value("${GMAIL_SENDER:}")
    private String gmailSenderEmail;

    @Value("${MAIL_USERNAME:}")
    private String mailUsername;

    public boolean sendEmail(String to, String subject, String htmlContent) {
        if (!hasOAuthConfig()) {
            log.warn("[GmailService] Configuracion OAuth incompleta. Revisa GMAIL_CLIENT_ID, GMAIL_CLIENT_SECRET, GMAIL_REFRESH_TOKEN y GMAIL_SENDER.");
            return false;
        }

        try {
            GoogleCredentials credentials = buildCredentials();
            Gmail gmail = buildGmailClient(credentials);
            Message message = buildMimeMessage(to, subject, htmlContent);

            gmail.users().messages().send("me", message).execute();
            log.info("[GmailService] Correo enviado exitosamente a {} via Gmail API.", to);
            return true;
        } catch (Exception e) {
            log.error("[GmailService] Error al enviar correo a {} via Gmail API: {}", to, e.getMessage(), e);
            return false;
        }
    }

    private GoogleCredentials buildCredentials() {
        return UserCredentials.newBuilder()
                .setClientId(clientId.trim())
                .setClientSecret(clientSecret.trim())
                .setRefreshToken(refreshToken.trim())
                .build()
                .createScoped(Collections.singletonList(GMAIL_SEND_SCOPE));
    }

    private Gmail buildGmailClient(GoogleCredentials credentials) throws Exception {
        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Message buildMimeMessage(String to, String subject, String htmlContent) throws Exception {
        String senderEmail = resolveSenderEmail();
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

    private boolean hasOAuthConfig() {
        return isPresent(clientId)
                && isPresent(clientSecret)
                && isPresent(refreshToken)
                && isPresent(resolveSenderEmail());
    }

    private String resolveSenderEmail() {
        if (isPresent(gmailSenderEmail)) {
            return gmailSenderEmail.trim();
        }
        if (isPresent(mailUsername)) {
            return mailUsername.trim();
        }
        return "volticfit@gmail.com";
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
