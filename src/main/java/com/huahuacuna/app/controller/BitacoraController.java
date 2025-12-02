// src/main/java/com/huahuacuna/app/controller/BitacoraController.java
package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.BitacoraDTO;
import com.huahuacuna.app.DTO.BitacoraResponseDTO;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.BitacoraService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bitacora")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BitacoraController {

    @Autowired
    private BitacoraService bitacoraService;

    // Obtener bitácora de un niño
    @GetMapping("/nino/{ninoId}")
    public ResponseEntity<?> obtenerPorNino(
            @PathVariable Integer ninoId,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        try {
            List<BitacoraResponseDTO> entradas = bitacoraService.obtenerPorNino(ninoId);
            return ResponseEntity.ok(entradas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // Obtener una entrada específica
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @PathVariable Integer id,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        try {
            BitacoraResponseDTO entrada = bitacoraService.obtenerPorId(id);
            return ResponseEntity.ok(entrada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // Crear nueva entrada (solo admin)
    @PostMapping("/nino/{ninoId}")
    public ResponseEntity<?> crearEntrada(
            @PathVariable String ninoId,
            @Valid @RequestBody BitacoraDTO dto,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (usuario.getRol() != Usuario.Rol.administrador) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "Solo los administradores pueden crear entradas"));
        }

        try {
            Integer id = Integer.parseInt(ninoId);
            BitacoraResponseDTO entrada = bitacoraService.crearEntrada(id, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(entrada);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", "El ID del niño debe ser un número válido, recibido: " + ninoId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // Actualizar entrada (solo admin)
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarEntrada(
            @PathVariable Integer id,
            @Valid @RequestBody BitacoraDTO dto,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (usuario.getRol() != Usuario.Rol.administrador) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "Solo los administradores pueden editar entradas"));
        }

        try {
            BitacoraResponseDTO entrada = bitacoraService.actualizarEntrada(id, dto);
            return ResponseEntity.ok(entrada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // Eliminar entrada (solo admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEntrada(
            @PathVariable Integer id,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debe iniciar sesión"));
        }

        if (usuario.getRol() != Usuario.Rol.administrador) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "Solo los administradores pueden eliminar entradas"));
        }

        try {
            bitacoraService.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Entrada eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}