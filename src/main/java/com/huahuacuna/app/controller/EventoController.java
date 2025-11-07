package com.huahuacuna.app.controller;

import com.huahuacuna.app.DTO.EventoDTO;
import com.huahuacuna.app.DTO.InscripcionEventoDTO;
import com.huahuacuna.app.model.InscripcionEvento;
import com.huahuacuna.app.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // ========== CRUD EVENTOS ==========

    @PostMapping
    public ResponseEntity<EventoDTO> crear(@Valid @RequestBody EventoDTO dto) {
        EventoDTO creado = eventoService.crearEvento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<EventoDTO>> listarTodos() {
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EventoDTO>> listarActivos() {
        return ResponseEntity.ok(eventoService.listarActivos());
    }

    @GetMapping("/proximos")
    public ResponseEntity<List<EventoDTO>> listarProximos() {
        return ResponseEntity.ok(eventoService.listarProximosEventos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EventoDTO dto) {
        return ResponseEntity.ok(eventoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ========== INSCRIPCIONES ==========

    @PostMapping("/inscribirse")
    public ResponseEntity<InscripcionEvento> inscribirse(@Valid @RequestBody InscripcionEventoDTO dto) {
        InscripcionEvento inscripcion = eventoService.inscribirse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcion);
    }

    @GetMapping("/{id}/inscripciones")
    public ResponseEntity<List<InscripcionEvento>> obtenerInscripciones(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtenerInscripciones(id));
    }

    @GetMapping("/inscripciones")
    public ResponseEntity<List<InscripcionEvento>> listarTodasInscripciones() {
        return ResponseEntity.ok(eventoService.listarTodasInscripciones());
    }

    @PatchMapping("/inscripciones/{id}/estado")
    public ResponseEntity<InscripcionEvento> actualizarEstado(
            @PathVariable Long id,
            @RequestParam InscripcionEvento.EstadoInscripcion estado) {
        return ResponseEntity.ok(eventoService.actualizarEstadoInscripcion(id, estado));
    }
}