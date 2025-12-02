package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.ApadrinamientoDTO;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.ApadrinamientoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/apadrinamientos")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ApadrinamientoController {

    @Autowired
    private ApadrinamientoService apadrinamientoService;

    @PostMapping
    public ResponseEntity<?> crearApadrinamiento(
            @RequestBody Map<String, Integer> body,
            HttpSession session) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión para apadrinar"));
        }

        if (usuario.getRol() != Usuario.Rol.padrino) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "Solo los padrinos pueden apadrinar niños"));
        }

        Integer idNino = body.get("idNino");
        if (idNino == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Debe especificar el ID del niño"));
        }

        try {
            ApadrinamientoDTO apadrinamiento = apadrinamientoService
                    .crearApadrinamiento(usuario.getId_usuario(), idNino);
            return ResponseEntity.status(HttpStatus.CREATED).body(apadrinamiento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/mis-apadrinados")
    public ResponseEntity<?> obtenerMisApadrinados(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        List<ApadrinamientoDTO> apadrinamientos = apadrinamientoService
                .obtenerApadrinamientosPorPadrino(usuario.getId_usuario());
        
        return ResponseEntity.ok(apadrinamientos);
    }

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        List<ApadrinamientoDTO> apadrinamientos = apadrinamientoService
                .obtenerTodosApadrinamientosPorPadrino(usuario.getId_usuario());
        
        return ResponseEntity.ok(apadrinamientos);
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarApadrinamiento(
            @PathVariable Integer id,
            HttpSession session) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (usuario.getRol() != Usuario.Rol.administrador) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "Solo los administradores pueden finalizar apadrinamientos"));
        }

        try {
            ApadrinamientoDTO apadrinamiento = apadrinamientoService.finalizarApadrinamiento(id);
            return ResponseEntity.ok(apadrinamiento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/verificar/{idNino}")
    public ResponseEntity<?> verificarApadrinamiento(
            @PathVariable Integer idNino,
            HttpSession session) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        boolean existe = apadrinamientoService
                .existeApadrinamiento(usuario.getId_usuario(), idNino);
        
        return ResponseEntity.ok(Map.of("apadrinado", existe));
    }
}