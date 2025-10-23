package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.LoginDTO;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest, HttpSession session) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(loginRequest.getCorreo());

        if (usuario.isPresent() && usuario.get().getContrasena().equals(loginRequest.getContrasena())) {
            session.setAttribute("usuarioLogueado", usuario.get());

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Inicio de sesión exitoso",
                    "rol", usuario.get().getRol(),
                    "usuarioId", usuario.get().getId_usuario()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "mensaje", "Credenciales inválidas"
            ));
        }
    }
}
