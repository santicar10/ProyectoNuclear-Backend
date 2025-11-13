package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.NinoDTO;
import com.huahuacuna.app.model.Nino;
import com.huahuacuna.app.model.Usuario;
import com.huahuacuna.app.service.NinoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ninos")
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class NinoController {

    @Autowired
    private NinoService ninoService;

    private boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        return usuario != null && usuario.getRol() == Usuario.Rol.administrador;
    }

    @GetMapping
    public ResponseEntity<?> listarTodos(HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Acceso denegado: solo administradores."));
        }
        return ResponseEntity.ok(ninoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Acceso denegado: solo administradores."));
        }
        return ninoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Acceso denegado: solo administradores."));
        }
        ninoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint público para ver el perfil del niño
     */
    @GetMapping("/publico/{id}")
    public ResponseEntity<?> verNinoPublico(@PathVariable Integer id) {
        return ninoService.buscarPorId(id)
                .map(nino -> {
                    // Usar HashMap en lugar de Map.of() para permitir valores nulos
                    Map<String, Object> response = new HashMap<>();
                    response.put("id_nino", nino.getId_nino());
                    response.put("nombre", nino.getNombre() != null ? nino.getNombre() : "");
                    response.put("edad", nino.getEdad());
                    response.put("genero", nino.getGenero() != null ? nino.getGenero() : "");
                    response.put("descripcion", nino.getDescripcion() != null ? nino.getDescripcion() : "");
                    response.put("foto", nino.getFotoUrl()); 
                    response.put("fotoUrl", nino.getFotoUrl()); 
                    response.put("fechaNacimiento", nino.getFechaNacimiento() != null ? nino.getFechaNacimiento().toString() : null);
                    response.put("estadoApadrinamiento", nino.getEstadoApadrinamiento() != null ? nino.getEstadoApadrinamiento().toString() : "");
                    
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(404).body(Map.of("mensaje", "Niño no encontrado.")));
    }
}