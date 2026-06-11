package com.proyecto.volticfit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;

/**
 * Clase de configuración centralizada para las constantes de la aplicación Volticfit.
 * Maneja la inyección de variables externas desde el archivo .yaml y define
 * plantillas para servicios de mensajería y seguridad.
 */
@Component
@Log4j2
public class AppConstants {

 /** Frontend base URL for recovery links */
    public static String FRONTEND_URL;
 
    /** Sender email address */
    public static String MAIL_FROM;
 
    /** Token expiration time in milliseconds */
    public static Long TOKEN_EXPIRATION_TIME;
 
    /** Recovery token expiration time in minutes (for email display) */
    public static int RECOVERY_EXPIRATION_MINUTES;
 
    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;
 
    @Value("${spring.mail.username:volticfit@gmail.com}")
    private String mailFrom;
 
    @Value("${security.jwt.token-expiration:3600000}")
    private Long tokenExpiration;
 
    @Value("${security.jwt.recovery-expiration-minutes:15}")
    private int recoveryExpirationMinutes;
 
    /** Subject for password recovery emails */
    public static final String RECOVERY_SUBJECT = "Recuperación de Contraseña - VolticFit";

    /** HTML template for recovery email. Parameters: code, expiration minutes */
    public static final String RECOVERY_HTML_TEMPLATE =
        "<!DOCTYPE html>" +
        "<html lang=\"es\">" +
        "<head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"></head>" +
        "<body style=\"margin:0;padding:0;background:#f4f4f4;font-family:Arial,sans-serif;\">" +
        "  <table width=\"100%%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#f4f4f4;padding:40px 0;\">" +
        "    <tr><td align=\"center\">" +
        "      <table width=\"560\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#1a1a1a;border-radius:12px;overflow:hidden;\">" +
        "        <!-- Header -->" +
        "        <tr><td style=\"background:#1a1a1a;padding:32px 40px 24px;text-align:center;border-bottom:3px solid #f5a623;\">" +
        "          <span style=\"font-size:28px;font-weight:900;color:#f5a623;letter-spacing:2px;\">VOLTIC FIT</span><br>" +
        "          <span style=\"font-size:12px;color:#888;letter-spacing:1px;\">GESTIÓN DE GIMNASIO</span>" +
        "        </td></tr>" +
        "        <!-- Body -->" +
        "        <tr><td style=\"padding:36px 40px;\">" +
        "          <p style=\"color:#ffffff;font-size:18px;font-weight:bold;margin:0 0 8px;\">Recuperación de contraseña</p>" +
        "          <p style=\"color:#aaaaaa;font-size:14px;margin:0 0 28px;\">Hemos recibido una solicitud para restablecer la contraseña de tu cuenta. Usa el siguiente código de verificación:</p>" +
        "          <!-- Code box -->" +
        "          <div style=\"background:#242424;border:2px solid #f5a623;border-radius:10px;padding:24px;text-align:center;margin:0 0 28px;\">" +
        "            <span style=\"font-size:42px;font-weight:900;letter-spacing:12px;color:#f5a623;\">%s</span>" +
        "          </div>" +
        "          <p style=\"color:#aaaaaa;font-size:13px;margin:0 0 24px;\">Este código expirará en <strong style=\"color:#ffffff;\">%d minutos</strong>. Si no solicitaste este cambio, puedes ignorar este mensaje con tranquilidad.</p>" +
        "          <p style=\"color:#f5a623;font-size:13px;font-weight:bold;margin:0 0 24px;\">Si no ves este correo en tu bandeja de entrada, revisa tambien la carpeta de spam o correo no deseado.</p>" +
        "          <div style=\"background:#2a2a2a;border-radius:8px;padding:14px 18px;border-left:4px solid #f5a623;\">" +
        "            <p style=\"color:#aaa;font-size:12px;margin:0;\">⚠️ Por seguridad, nunca compartas este código con nadie. VolticFit jamás te lo pedirá.</p>" +
        "          </div>" +
        "        </td></tr>" +
        "        <!-- Footer -->" +
        "        <tr><td style=\"background:#111;padding:20px 40px;text-align:center;border-top:1px solid #2a2a2a;\">" +
        "          <p style=\"color:#555;font-size:12px;margin:0;\">© 2025 VolticFit · Todos los derechos reservados</p>" +
        "        </td></tr>" +
        "      </table>" +
        "    </td></tr>" +
        "  </table>" +
        "</body></html>";
 
    /**
     * Body template for the recovery email.
     * Parameters: frontend URL, token, expiration minutes.
     */
    public static final String RECOVERY_BODY_TEMPLATE =
        "Hola,\n\nHas solicitado restablecer tu contraseña en Volticfit. " +
        "Haz clic en el siguiente enlace para continuar:\n\n%s/auth/reset-password?token=%s" +
        "\n\nEste enlace expirara en %d minutos. Si no fuiste tu, ignora este mensaje." +
        "\n\nSi no ves este correo en tu bandeja de entrada, revisa tambien spam o correo no deseado.";

    /**
     * Template for 6-digit code recovery email.
     * Parameters: code, expiration minutes.
     */
    public static final String RECOVERY_CODE_TEMPLATE =
        "Hola,\n\nTu código de recuperación de contraseña es:\n\n%s" +
        "\n\nEste codigo expirara en %d minutos. Si no fuiste tu, ignora este mensaje." +
        "\n\nSi no ves este correo en tu bandeja de entrada, revisa tambien spam o correo no deseado.";
 
    /**
     * Initializes static variables after Spring injects @Value fields.
     */
    @PostConstruct
    public void init() {
        FRONTEND_URL = frontendUrl;
        MAIL_FROM = mailFrom;
        TOKEN_EXPIRATION_TIME = tokenExpiration;
        RECOVERY_EXPIRATION_MINUTES = recoveryExpirationMinutes;
        log.info("Application constants loaded successfully.");

    }
}
