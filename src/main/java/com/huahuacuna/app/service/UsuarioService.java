package com.huahuacuna.app.service;

import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordService passwordService;

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        usuario.setFecha_creacion(LocalDate.now());
        usuario.setEstado(Usuario.Estado.activo);

        return usuarioRepository.save(usuario);
    }

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
}





