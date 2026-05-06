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
    public static final String RECOVERY_SUBJECT = "🔑 Recuperación de Contraseña - Volticfit";
 
    /**
     * Body template for the recovery email.
     * Parameters: frontend URL, token, expiration minutes.
     */
    public static final String RECOVERY_BODY_TEMPLATE =
        "Hola,\n\nHas solicitado restablecer tu contraseña en Volticfit. " +
        "Haz clic en el siguiente enlace para continuar:\n\n%s/auth/reset-password?token=%s" +
        "\n\nEste enlace expirará en %d minutos. Si no fuiste tú, ignora este mensaje.";

    /**
     * Template for 6-digit code recovery email.
     * Parameters: code, expiration minutes.
     */
    public static final String RECOVERY_CODE_TEMPLATE =
        "Hola,\n\nTu código de recuperación de contraseña es:\n\n%s" +
        "\n\nEste código expirará en %d minutos. Si no fuiste tú, ignora este mensaje.";
 
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