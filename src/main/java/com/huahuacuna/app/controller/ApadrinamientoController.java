package com.huahuacuna.app.controller;


import com.huahuacuna.app.model.Apadrinamiento;
import com.huahuacuna.app.model.Nino;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.ApadrinamientoService;
import com.huahuacuna.app.service.NinoService;
import com.huahuacuna.app.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/apadrinamientos")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ApadrinamientoController {

    @Autowired
    private ApadrinamientoService apadrinamientoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private NinoService ninoService;


    @PostMapping
    public ResponseEntity<?> apadrinar(@RequestBody Map<String, Integer> request,
                                       HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || usuario.getRol() != Usuario.Rol.padrino) {
            return ResponseEntity.status(403)
                    .body(Map.of("mensaje", "Solo el padrino puede apadrinar."));
        }

        Integer idNino = request.get("idNino");

        Nino nino = ninoService.buscarPorId(idNino)
                .orElse(null);

        if (nino == null) {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Niño no encontrado"));
        }

        Apadrinamiento ap = new Apadrinamiento();
        ap.setPadrino(usuario);
        ap.setNino(nino);
        ap.setFechaInicio(LocalDate.now());
        ap.setEstado(Apadrinamiento.EstadoApadrinamiento.ACTIVO);

        Apadrinamiento saved = apadrinamientoService.guardar(ap);

        return ResponseEntity.ok(saved);
    }


    @GetMapping("/mis-ahijados")
    public ResponseEntity<?> verMisAhijados(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || usuario.getRol() != Usuario.Rol.padrino) {
            return ResponseEntity.status(403)
                    .body(Map.of("mensaje", "Solo el padrino puede ver sus ahijados."));
        }

        return ResponseEntity.ok(apadrinamientoService.buscarPorPadrino(usuario.getIdUsuario()));
    }


    @GetMapping
    public ResponseEntity<?> listarTodos(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null || usuario.getRol() != Usuario.Rol.administrador) {
            return ResponseEntity.status(403)
                    .body(Map.of("mensaje", "Solo administradores."));
        }

        return ResponseEntity.ok(apadrinamientoService.listarTodos());
    }
}

