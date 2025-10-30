package com.huahuacuna.app.service;

import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio que encapsula la lógica de negocio relacionada con usuarios:
 * - Registro de usuarios
 * - Recuperación de contraseña (generar nueva contraseña y notificar por correo)
 *
 * Notas:
 * - Actualmente las contraseñas se asignan directamente en texto plano. Es imprescindible
 *   almacenar hashes seguros (BCrypt) al persistir.
 * - Recomendado añadir anotación {@code @Transactional} en operaciones compuestas que
 *   modifiquen varias entidades o requieran consistencia transaccional.
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordService passwordService;

    /**
     * Registra un nuevo usuario.
     *
     * Flujo:
     *  - Verifica si ya existe un usuario con el mismo correo (existsByCorreo).
     *  - Rellena fecha_creacion y estado.
     *  - Persiste la entidad.
     *
     * @param usuario entidad Usuario a registrar (se espera que contenga al menos correo y contrasena)
     * @return Usuario persistido (con id generado)
     * @throws IllegalArgumentException si el correo ya está registrado
     *
     * Mejora recomendada: antes de guardar, hashear la contraseña (BCrypt) y validar campos con DTOs.
     */
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        usuario.setFecha_creacion(LocalDate.now());
        usuario.setEstado(Usuario.Estado.activo);

        // Asignar rol por defecto si no viene proporcionado (usar la constante del enum Usuario.Rol)
        if (usuario.getRol() == null) {
            usuario.setRol(Usuario.Rol.voluntario);
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Comprueba si ya existe un usuario con el correo dado.
     * Útil para validaciones desde controladores.
     */
    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    /**
     * Recupera la contraseña de un usuario identificado por correo.
     *
     * Flujo:
     *  - Busca el usuario por correo.
     *  - Si no existe, retorna false.
     *  - Genera una nueva contraseña con PasswordService.
     *  - Asigna la nueva contraseña a la entidad y la guarda.
     *  - Intenta enviar la nueva contraseña por correo usando EmailService.
     *
     * @param correo correo del usuario
     * @return true si se envió la notificación de recuperación correctamente, false en caso contrario
     *
     * Mejoras recomendadas:
     *  - En lugar de setear y enviar la contraseña en texto plano, generar un token de recuperación
     *    con expiración y enviar un enlace para restablecer la contraseña.
     *  - Hashear la contraseña antes de persistir.
     *  - Hacer el envío de correo de forma asíncrona y lanzar o registrar adecuadamente los errores.
     */
    public boolean recuperarContrasena(String correo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();

        String nuevaContrasena = passwordService.generarContrasenaAleatoria();
        usuario.setContrasena(nuevaContrasena);
        usuarioRepository.save(usuario);

        try {
            emailService.enviarRecuperacion(usuario.getCorreo(), usuario.getNombre(), nuevaContrasena);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void iniciarRecuperacion(String correo) throws MessagingException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        Usuario usuario = usuarioOpt.get();

        // Generar código numérico de 6 dígitos
        String codigo = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        // Guardar código y expiración (por ejemplo 15 minutos)
        usuario.setRecoveryCode(codigo);
        usuario.setRecoveryExpiry(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        // Enviar correo (reutiliza EmailService.enviarRecuperacion pero ajusta el cuerpo)
        emailService.enviarRecuperacionCodigo(usuario.getCorreo(), usuario.getNombre(), codigo);
    }

    public boolean verificarCodigo(String correo, String codigo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        Usuario usuario = usuarioOpt.get();
        if (usuario.getRecoveryCode() == null || usuario.getRecoveryExpiry() == null) {
            return false;
        }
        if (!usuario.getRecoveryCode().equals(codigo)) {
            return false;
        }
        if (usuario.getRecoveryExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    public boolean resetContrasena(String correo, String codigo, String nuevaContrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        Usuario usuario = usuarioOpt.get();
        if (usuario.getRecoveryCode() == null || usuario.getRecoveryExpiry() == null) {
            return false;
        }
        if (!usuario.getRecoveryCode().equals(codigo)) {
            return false;
        }
        if (usuario.getRecoveryExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Aquí: cambiar contraseña. Recomiendo hashear con BCrypt. Ejemplo simple:
        usuario.setContrasena(nuevaContrasena); // <- reemplazar por passwordEncoder.encode(nuevaContrasena) si usas BCrypt
        // Limpiar los campos de recuperación
        usuario.setRecoveryCode(null);
        usuario.setRecoveryExpiry(null);
        usuarioRepository.save(usuario);
        return true;
    }

    /**
     * Cambia la contraseña de un usuario identificado por idUsuario.
     * Verifica que la contrasenaActual coincida con la guardada.
     *
     * @param idUsuario id del usuario a modificar
     * @param contrasenaActual contraseña proporcionada por el usuario
     * @param nuevaContrasena nueva contraseña a guardar
     * @return true si se cambió la contraseña, false si la actual no coincide o el usuario no existe
     */
    public boolean cambiarContrasena(Integer idUsuario, String contrasenaActual, String nuevaContrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();

        // Comparación directa (texto plano). Si migras a hashes, usar PasswordEncoder.matches(...)
        if (!usuario.getContrasena().equals(contrasenaActual)) {
            return false;
        }

        usuario.setContrasena(nuevaContrasena);
        usuarioRepository.save(usuario);
        return true;
    }
}