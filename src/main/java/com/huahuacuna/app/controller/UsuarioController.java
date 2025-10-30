package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.CambiarContrasenaDTO;
import com.huahuacuna.app.DTO.RecuperarDTO;
import com.huahuacuna.app.DTO.RegistroDTO;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.UsuarioService;
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
    public ResponseEntity<?> recuperar(@RequestBody RecuperarDTO request) {
        boolean resultado = usuarioService.recuperarContrasena(request.getCorreo());
        if (resultado) {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Se ha enviado una nueva contraseña a tu correo."
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "mensaje", "No se encontró ningún usuario con ese correo."
            ));
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