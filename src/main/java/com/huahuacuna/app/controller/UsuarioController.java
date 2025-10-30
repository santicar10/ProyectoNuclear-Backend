package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.*;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.UsuarioService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000\", allowCredentials = \"true")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroDTO dto) {
        String correo = dto.getCorreo().trim().toLowerCase();

        if (usuarioService.existsByCorreo(correo)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "El correo ya está registrado."));
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setNombre(dto.getNombre().trim());
        usuario.setContrasena(dto.getContrasena());

        Usuario saved = usuarioService.registrar(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "usuarioId", saved.getId_usuario(),
                "correo", saved.getCorreo(),
                "nombre", saved.getNombre()
        ));
    }

    @PostMapping("/recuperar")
    public ResponseEntity<?> iniciarRecuperacion(@Valid @RequestBody RecuperarRequestDTO dto) {
        try {
            usuarioService.iniciarRecuperacion(dto.getCorreo());
            // Por seguridad no confirmamos si el correo existe o no en la respuesta pública.
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, se ha enviado un código de recuperación."));
        } catch (IllegalArgumentException e) {
            // Podemos devolver 200 con el mismo mensaje para evitar enumeración de usuarios
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, se ha enviado un código de recuperación."));
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("mensaje", "Error enviando el correo."));
        }
    }

    @PostMapping("/recuperar/verificar")
    public ResponseEntity<?> verificarCodigo(@Valid @RequestBody VerificarCodigoDTO dto) {
        boolean ok = usuarioService.verificarCodigo(dto.getCorreo(), dto.getCodigo());
        if (ok) {
            return ResponseEntity.ok(Map.of("mensaje", "Código válido."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", "Código inválido o expirado."));
        }
    }

    @PostMapping("/recuperar/reset")
    public ResponseEntity<?> resetContrasena(@Valid @RequestBody ResetContrasenaDTO dto) {
        boolean ok = usuarioService.resetContrasena(dto.getCorreo(), dto.getCodigo(), dto.getNuevaContrasena());
        if (ok) {
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña restablecida correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", "Código inválido o expirado."));
        }
    }

    @PostMapping("/cambiar")
    public ResponseEntity<?> cambiarContrasena(@Valid @RequestBody CambiarContrasenaDTO dto, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Debe iniciar sesión para cambiar la contraseña."));
        }

        boolean cambiado = usuarioService.cambiarContrasena(usuario.getId_usuario(), dto.getContrasenaActual(), dto.getNuevaContrasena());
        if (cambiado) {
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña cambiada correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", "La contraseña actual es incorrecta."));
        }
    }


    @GetMapping("/perfil")
    public ResponseEntity<?> verPerfil(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "mensaje", "Debe iniciar sesión para ver su perfil."
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> cerrarSesion(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of(
                "mensaje", "Sesión cerrada correctamente."
        ));
    }
}