package com.huahuacuna.app.service;

import com.huahuacuna.app.Utils.EmailUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailUtil emailUtil;

    @Value("${app.mail.username}")
    private String fromEmail;

    @Value("${app.mail.password}")
    private String mailPassword;

    public EmailService(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    public void enviarRecuperacion(String toEmail, String nombre, String nuevaContrasena) throws MessagingException {
        String subject = "Recuperación de contraseña - Fundación Huahuacuna";
        String body = "Hola " + nombre + ",\n\nTu nueva contraseña es: " + nuevaContrasena +
                "\n\nPor favor cámbiala cuando inicies sesión.";
        emailUtil.sendEmail(fromEmail, mailPassword, toEmail, subject, body);
    }
}

