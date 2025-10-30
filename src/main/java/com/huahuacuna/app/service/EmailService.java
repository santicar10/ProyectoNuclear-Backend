package com.huahuacuna.app.service;

import com.huahuacuna.app.Utils.EmailUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio responsable del envío de correos relacionados con usuarios
 * (por ejemplo, notificaciones de recuperación de contraseña).
 *
 * <p>Este servicio delega el envío real de correo a {@link EmailUtil} y obtiene
 * la dirección remitente y la contraseña desde propiedades de configuración
 * (propiedades prefijadas con {@code app.mail.*}).</p>
 *
 * Notas:
 * - Las credenciales de correo (usuario/contraseña) se inyectan con {@link Value}
 *   desde el archivo de configuración (application.properties / application.yml).
 * - Evitar mantener credenciales en texto plano en el repo: usar variables de entorno
 *   o un secret manager y referenciarlas en las propiedades.
 */
@Service
public class EmailService {

    private final EmailUtil emailUtil;

    /**
     * Dirección de correo remitente (inyectada desde configuración con key: app.mail.username).
     */
    @Value("${app.mail.username}")
    private String fromEmail;

    /**
     * Contraseña o token para la cuenta de correo (inyectado desde app.mail.password).
     *
     * Nota de seguridad: preferir tokens o variables de entorno, y nunca commitear contraseñas.
     */
    @Value("${app.mail.password}")
    private String mailPassword;

    public EmailService(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    /**
     * Envía un correo de recuperación de contraseña.
     *
     * @param toEmail       dirección de destino
     * @param nombre        nombre del usuario (para saludo personalizado)
     * @param nuevaContrasena la contraseña nueva (o token) que será enviada
     * @throws MessagingException si ocurre un error en el envío
     */
    public void enviarRecuperacion(String toEmail, String nombre, String nuevaContrasena) throws MessagingException {
        String subject = "Recuperación de contraseña - Fundación Huahuacuna";
        String body = "Hola " + nombre + ",\n\nTu nueva contraseña es: " + nuevaContrasena +
                "\n\nPor favor cámbiala cuando inicies sesión.";
        emailUtil.sendEmail(fromEmail, mailPassword, toEmail, subject, body);
    }

    public void enviarRecuperacionCodigo(String toEmail, String nombre, String codigo) throws MessagingException {
        String subject = "Código de recuperación - Fundación Huahuacuna";
        String body = "Hola " + nombre + ",\n\nTu código de recuperación es: " + codigo +
                "\n\nIngresa este código en la aplicación para poder restablecer tu contraseña. El código expira en 15 minutos.";
        emailUtil.sendEmail(fromEmail, mailPassword, toEmail, subject, body);
    }
}