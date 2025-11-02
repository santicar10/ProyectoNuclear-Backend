package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.ProyectoDTO;
import com.huahuacuna.app.model.Proyecto;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.ProyectoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de proyectos (solo ADMIN).
 */
@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    /**
     * Listar todos los proyectos (solo admin logueado).
     */
    @GetMapping
    public ResponseEntity<?> listarTodos(HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body("Acceso denegado: solo administradores.");
        }
        return ResponseEntity.ok(proyectoService.listarTodos());
    }

    /**
     * Listar solo proyectos activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<?> listarActivos() {
        return ResponseEntity.ok(proyectoService.listarActivos());
    }

    /**
     * Obtener un proyecto por ID (solo admin logueado).
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body("Acceso denegado: solo administradores.");
        }
        return proyectoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crear un nuevo proyecto (solo admin logueado).
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProyectoDTO dto, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body("Acceso denegado: solo administradores.");
        }
        Proyecto nuevo = proyectoService.guardar(dto);
        return ResponseEntity.ok(nuevo);
    }

    /**
     * Actualizar un proyecto existente (solo admin logueado).
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody ProyectoDTO dto, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body("Acceso denegado: solo administradores.");
        }
        Proyecto actualizado = proyectoService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Eliminar un proyecto (solo admin logueado).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body("Acceso denegado: solo administradores.");
        }
        proyectoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifica si el usuario logueado es administrador.
     */
    private boolean esAdmin(HttpSession session) {
        Object usuario = session.getAttribute("usuarioLogueado");
        if (usuario instanceof com.huahuacuna.app.model.Usuario user) {
            return user.getRol() == Usuario.Rol.administrador;
        }
        return false;

    }
}

