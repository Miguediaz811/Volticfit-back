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

    // Variables estáticas para acceso global (Requisito: no quemadas)
    public static String FRONTEND_URL;
    public static String MAIL_FROM;
    public static Long TOKEN_EXPIRATION_TIME;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    // Se agrega valor por defecto para evitar el error de arranque si no hay config de mail
    @Value("${spring.mail.username:noreply@volticfit.com}")
    private String mailFrom;

    /** Tiempo de expiración del token JWT (inyectado desde yaml) */
    @Value("${project.jwt.expiration:3600000}")
    private Long tokenExpiration;

    // Constantes de texto (Requisito: JavaDoc y final)
    
    /** Asunto predefinido para correos de recuperación de cuenta */
    public static final String RECOVERY_SUBJECT = "🔑 Recuperación de Contraseña - Volticfit";

    /** Plantilla de cuerpo para el correo de recuperación de contraseña */
    public static final String RECOVERY_BODY_TEMPLATE = 
        "Hola,\n\nHas solicitado restablecer tu contraseña en Volticfit. " +
        "Haz clic en el siguiente enlace para continuar:\n\n%s/auth/reset-password?token=%s" +
        "\n\nEste enlace expirará en %d minutos. Si no fuiste tú, ignora este mensaje.";

    /**
     * Inicializa las variables estáticas después de que Spring inyecta los valores @Value.
     * Utiliza Log4j2 para confirmar la carga exitosa (Requisito: No System.out.print).
     */
    @PostConstruct
    public void init() {
        FRONTEND_URL = frontendUrl;
        MAIL_FROM = mailFrom;
        TOKEN_EXPIRATION_TIME = tokenExpiration;
        
        log.info("🚀 Constantes de aplicación cargadas correctamente.");
        log.info("🔗 Frontend URL configurada: {}", FRONTEND_URL);
        log.info("⏰ Token Expiration configurado: {} ms", TOKEN_EXPIRATION_TIME);
    }
}