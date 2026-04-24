package com.proyecto.volticfit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoRecuperacion(String destinatario, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatario);
            message.setFrom("mgomezgomez2008@gmail.com"); // Tu correo del YAML
            message.setSubject("🔑 Recuperación de Contraseña - Volticfit");
            
            // Link para tu Angular en el MacBook
            String url = "http://localhost:4200/auth/reset-password?token=" + token;
            
            message.setText("Hola,\n\nHas solicitado restablecer tu contraseña en Volticfit. " +
                            "Haz clic en el siguiente enlace para continuar:\n\n" + url + 
                            "\n\nEste enlace expirará en 15 minutos. Si no fuiste tú, ignora este mensaje.");
            
            mailSender.send(message);
            System.out.println("Correo de recuperación enviado a: " + destinatario);
        } catch (Exception e) {
            System.err.println("Error crítico al enviar correo: " + e.getMessage());
        }
    }
}