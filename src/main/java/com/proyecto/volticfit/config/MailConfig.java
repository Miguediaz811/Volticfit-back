package com.proyecto.volticfit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuración dinámica para el servicio de mensajería de VolticFit.
 * Soporta STARTTLS en puerto 587 con propiedades de fiabilidad para
 * entornos de producción (Railway / contenedores).
 */
@Configuration
public class MailConfig {

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    /**
     * Habilita el log de sesión SMTP de JavaMail cuando la variable de entorno
     * SPRING_MAIL_DEBUG está definida como "true". Útil para diagnosticar
     * problemas de conectividad en producción sin necesidad de redesplegar.
     */
    @Value("${spring.mail.debug:false}")
    private boolean mailDebug;

    /**
     * Define el bean de JavaMailSender utilizando las credenciales inyectadas.
     *
     * @return JavaMailSender configurado para Gmail SMTP con STARTTLS
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();

        // Transport & auth
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");

        // STARTTLS (upgrade plain connection to TLS on port 587)
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        // Timeouts (ms) — prevent silent hangs in containerised environments
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        // JavaMail session debug — toggled via SPRING_MAIL_DEBUG env var
        props.put("mail.debug", String.valueOf(mailDebug));

        return mailSender;
    }
}