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

/**
 * Controlador REST para la gestión de eventos e inscripciones.
 *
 * <p>Exponde endpoints para crear, listar, actualizar y eliminar eventos,
 * además de manejar inscripciones a eventos y la gestión de su estado.</p>
 *
 * Rutas principales:
 * - POST   /api/eventos                 : crear un evento
 * - GET    /api/eventos                 : listar todos los eventos
 * - GET    /api/eventos/activos         : listar eventos activos
 * - GET    /api/eventos/proximos        : listar eventos próximos
 * - GET    /api/eventos/{id}            : obtener evento por id
 * - PUT    /api/eventos/{id}            : actualizar evento
 * - DELETE /api/eventos/{id}            : eliminar evento
 * - POST   /api/eventos/inscribirse     : crear una inscripción a un evento
 * - GET    /api/eventos/{id}/inscripciones : obtener inscripciones de un evento
 * - GET    /api/eventos/inscripciones   : listar todas las inscripciones
 * - PATCH  /api/eventos/inscripciones/{id}/estado : actualizar estado de una inscripción
 *
 * @see EventoService
 * @see EventoDTO
 * @see InscripcionEventoDTO
 * @author Janka033
 * @since 1.0
 */
@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    /**
     * Servicio con la lógica de negocio relacionada a eventos e inscripciones.
     * Inyectado por Spring.
     */
    @Autowired
    private EventoService eventoService;

    // ========== CRUD EVENTOS ==========

    /**
     * Crea un nuevo evento a partir de la información recibida en el DTO.
     *
     * @param dto DTO con los datos del evento. Debe ser válido según las anotaciones de validación.
     * @return ResponseEntity con el EventoDTO creado y estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<EventoDTO> crear(@Valid @RequestBody EventoDTO dto) {
        EventoDTO creado = eventoService.crearEvento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Lista todos los eventos existentes (sin filtro).
     *
     * @return ResponseEntity con la lista de EventoDTO y estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<EventoDTO>> listarTodos() {
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    /**
     * Lista los eventos marcados como activos.
     *
     * @return ResponseEntity con la lista de eventos activos y estado HTTP 200 (OK).
     */
    @GetMapping("/activos")
    public ResponseEntity<List<EventoDTO>> listarActivos() {
        return ResponseEntity.ok(eventoService.listarActivos());
    }

    /**
     * Lista los eventos próximos (por fecha o criterio definido en el servicio).
     *
     * @return ResponseEntity con la lista de próximos eventos y estado HTTP 200 (OK).
     */
    @GetMapping("/proximos")
    public ResponseEntity<List<EventoDTO>> listarProximos() {
        return ResponseEntity.ok(eventoService.listarProximosEventos());
    }

    /**
     * Obtiene un evento por su identificador.
     *
     * @param id Identificador del evento.
     * @return ResponseEntity con el EventoDTO correspondiente y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtenerPorId(id));
    }

    /**
     * Actualiza un evento existente con los datos proporcionados.
     *
     * @param id  Identificador del evento a actualizar.
     * @param dto DTO con los nuevos datos del evento. Debe ser válido.
     * @return ResponseEntity con el EventoDTO actualizado y estado HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EventoDTO dto) {
        return ResponseEntity.ok(eventoService.actualizar(id, dto));
    }

    /**
     * Elimina un evento por su identificador.
     *
     * @param id Identificador del evento a eliminar.
     * @return ResponseEntity vacío con estado HTTP 204 (NO CONTENT) si la eliminación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ========== INSCRIPCIONES ==========

    /**
     * Registra una inscripción a un evento usando la información del DTO.
     *
     * @param dto DTO con los datos de inscripción (por ejemplo: eventoId, datos del usuario).
     * @return ResponseEntity con la entidad InscripcionEvento creada y estado HTTP 201 (CREATED).
     */
    @PostMapping("/inscribirse")
    public ResponseEntity<InscripcionEvento> inscribirse(@Valid @RequestBody InscripcionEventoDTO dto) {
        InscripcionEvento inscripcion = eventoService.inscribirse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcion);
    }

    /**
     * Obtiene la lista de inscripciones asociadas a un evento.
     *
     * @param id Identificador del evento.
     * @return ResponseEntity con la lista de InscripcionEvento y estado HTTP 200 (OK).
     */
    @GetMapping("/{id}/inscripciones")
    public ResponseEntity<List<InscripcionEvento>> obtenerInscripciones(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtenerInscripciones(id));
    }

    /**
     * Lista todas las inscripciones de todos los eventos (sin filtro).
     *
     * @return ResponseEntity con la lista de InscripcionEvento y estado HTTP 200 (OK).
     */
    @GetMapping("/inscripciones")
    public ResponseEntity<List<InscripcionEvento>> listarTodasInscripciones() {
        return ResponseEntity.ok(eventoService.listarTodasInscripciones());
    }

    /**
     * Actualiza el estado de una inscripción.
     *
     * <p>Ejemplo: PATCH /api/eventos/inscripciones/45/estado?estado=CONFIRMADA</p>
     *
     * @param id     Identificador de la inscripción.
     * @param estado Nuevo estado a asignar (enum {@link InscripcionEvento.EstadoInscripcion}).
     * @return ResponseEntity con la InscripcionEvento actualizada y estado HTTP 200 (OK).
     */
    @PatchMapping("/inscripciones/{id}/estado")
    public ResponseEntity<InscripcionEvento> actualizarEstado(
            @PathVariable Long id,
            @RequestParam InscripcionEvento.EstadoInscripcion estado) {
        return ResponseEntity.ok(eventoService.actualizarEstadoInscripcion(id, estado));
    }
}