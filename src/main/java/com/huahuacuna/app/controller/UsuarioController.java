package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.RecuperarDTO;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public Usuario registrar(@RequestBody Usuario usuario) {
        return usuarioService.registrar(usuario);
    }

    @PostMapping("/recuperar")
    public String recuperar(@RequestBody RecuperarDTO request) {
        boolean resultado = usuarioService.recuperarContrasena(request.getCorreo());
        if (resultado) {
            return "Se ha enviado una nueva contraseña a tu correo.";
        } else {
            return "No se encontró ningún usuario con ese correo.";
        }
    }


    @GetMapping("/perfil")
    public Object verPerfil(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            return usuario;
        } else {
            return "Debe iniciar sesión para ver su perfil.";
        }
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "Sesión cerrada correctamente.";
    }

}




