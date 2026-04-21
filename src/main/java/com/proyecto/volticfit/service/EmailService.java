package com.proyecto.volticfit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoRecuperacion(String destinatario, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatario);
            message.setSubject("🔑 Recuperación de Contraseña - Volticfit");
            
            // Link que recibirá el usuario
            String url = "http://localhost:4200/reset-password?token=" + token;
            
            message.setText("Hola,\n\nHas solicitado restablecer tu contraseña en Volticfit. " +
                            "Haz clic en el siguiente enlace para continuar:\n\n" + url + 
                            "\n\nEste enlace expirará en 15 minutos. Si no fuiste tú, ignora este mensaje.");
            
            mailSender.send(message);
            System.out.println("✅ Correo de recuperación enviado a: " + destinatario);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar correo: " + e.getMessage());
        }
    }
}