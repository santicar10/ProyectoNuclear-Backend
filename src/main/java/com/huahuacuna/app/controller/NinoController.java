package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.NinoDTO;
import com.huahuacuna.app.model.Nino;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.NinoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de niños.
 * Solo accesible para usuarios con rol "administrador".
 */
@RestController
@RequestMapping("/api/ninos")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class NinoController {

    @Autowired
    private NinoService ninoService;

    /**
     * Verifica si el usuario tiene permisos de administrador.
     */
    private boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        return usuario != null && usuario.getRol() == Usuario.Rol.administrador;
    }

    /**
     * Listar todos los niños.
     */
    @GetMapping
    public ResponseEntity<?> listarTodos(HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Acceso denegado: solo administradores."));
        }
        return ResponseEntity.ok(ninoService.listarTodos());
    }

    /**
     * Buscar un niño por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Acceso denegado: solo administradores."));
        }
        return ninoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crear un nuevo niño.
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody NinoDTO dto, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Acceso denegado: solo administradores."));
        }
        Nino nuevo = ninoService.guardar(dto);
        return ResponseEntity.ok(nuevo);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarParcialmente(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> campos,
            HttpSession session) {

        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Acceso denegado: solo administradores."));
        }

        try {
            Nino actualizado = ninoService.actualizarParcial(id, campos);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error al actualizar el niño."));
        }
    }


    /**
     * Eliminar un niño por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Acceso denegado: solo administradores."));
        }
        ninoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }


    /**metodo publico para ver el perfil del nino
     */
    @GetMapping("/publico/{id}")
    public ResponseEntity<?> verNinoPublico(@PathVariable Integer id) {
        return ninoService.buscarPorId(id)
                .map(nino -> Map.of(
                        "nombre", nino.getNombre(),
                        "edad", nino.getEdad(),
                        "genero", nino.getGenero(),
                        "foto", nino.getFotoUrl()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(Map.of("mensaje", "Niño no encontrado.")));
    }

}


